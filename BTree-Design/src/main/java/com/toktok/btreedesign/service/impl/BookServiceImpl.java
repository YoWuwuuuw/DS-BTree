package com.toktok.btreedesign.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.toktok.btreedesign.entity.dto.BookRecordDTO;
import com.toktok.btreedesign.entity.po.Book;
import com.toktok.btreedesign.entity.po.Record;
import com.toktok.btreedesign.entity.bo.RecordBo;
import com.toktok.btreedesign.mapper.BookMapper;
import com.toktok.btreedesign.service.BookService;
import com.toktok.btreedesign.service.RecordService;
import com.toktok.btreedesign.utils.BTree;
import com.toktok.btreedesign.utils.BookListContainer;
import com.toktok.btreedesign.utils.KeyValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService, ApplicationRunner {

    /**
     * 1. B树:存储文献，存储在内存中
     */
    private static BTree bTree;

    /**
     * 2. 存储Book容器，用于条件查询、返回全部等
     */
    private static BookListContainer bookListContainer = new BookListContainer();

    @Autowired
    private RecordService recordService;

    /**
     * 系统启动线程：在系统启动时加载数据库数据进入B树(内存)存储
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("正在初始化数据..");
        // 采用5阶b树存储
        bTree = new BTree(5);

        // 数据库数据加载进B树
        list().stream().forEach((Book book) -> {
            int hashCode = book.getBookName().hashCode();
            bTree.put(new KeyValue(hashCode, book));
        });

        // B树数据加载进List、HashSet
        bookListContainer.convertToBookList(bTree);
    }



    /**
     * 一、新增文献
     * @param book 实体类
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean addBook(Book book) {
        log.info(" '{}'文献正在入库..", book.getBookName());
        // 校验是否为空、库存量必须为正
        Assert.isTrue(!book.getBookName().isEmpty(), "传入文献不能为空");
        Assert.isTrue(book.getStock() > 0, "库存量必须为正数");

        // 查询B树中是否存在(即是否为新文献)
        boolean isTrue = bTree.contains(book.getBookName().hashCode());

        // 若无记录，则新增
        if (!isTrue) {
            // 新增book(自动回填id)
            this.save(book.setPutInAt(LocalDateTime.now()).setTotal(book.getStock()));
        } else {
            // 若有记录，则增加库存量、总库存量(根据新增文献批次的total、stock进行累加)
            Book one = this.getOne(new QueryWrapper<Book>().eq("book_name", book.getBookName()));
            book.setTotal(one.getTotal() + book.getStock()).setStock(one.getStock() + book.getStock());
            // 回填id
            book.setId(one.getId());
            book.setHot(one.getHot());

            // 更新数据库
            this.updateById(book);

            // 在过滤器中删除：
            bookListContainer.removeByBookName(book.getBookName());

            // 在b树中删除
            bTree.remove(new KeyValue(book.getBookName().hashCode(), null));
        }

        // 更新内存：B树
        bTree.put(new KeyValue(book.getBookName().hashCode(), book));
        return true;
    }


    /**
     * 二、通过文献名删除文献【省略dao层】
     * @param bookName 文献名
     * @return 是否成功
     */
    @Override
    public boolean deleteBookByBookName(String bookName) {
        log.info(" '{}'文献正在出库..", bookName);
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_name", bookName);
        // 若删除文献失效，则提示不存在文献
        Assert.isTrue(this.remove(queryWrapper), "不存在此文献");
        // 更新内存(Book传入null，因为用不到)
        bTree.remove(new KeyValue(bookName.hashCode(), null));

        bookListContainer.removeByBookName(bookName);
        return true;
    }


    /**
     * 三、借阅文献
     * @param recordBo 借阅记录bo
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean borrowBookByBookName(RecordBo recordBo) {
        log.info("{}正在借阅文献{}", recordBo.getUserName(), recordBo.getBook().getBookName());
        int key = recordBo.getBook().getBookName().hashCode();
        // 校验：文献是否存在
        Assert.isTrue(bTree.contains(key), "文献不存在");
        // 校验：是否重复借阅同一本书
        Assert.isTrue(!recordService.contains(key, recordBo.getUserName()), "已借阅该文献，禁止重复借阅");
        Book book = bTree.getBook(key);
        // 校验库存数是否为一
        Assert.isTrue(book.getStock() > 1, "库存为1，无法借阅");

        Record record = new Record();
        BeanUtils.copyProperties(recordBo, record);
        // 设置借阅时间为现在
        record.setBorrowAt(LocalDateTime.now());
        record.setBookKey(key);
        // 存储借阅记录
        Assert.isTrue(recordService.save(record), "服务器不稳定，请稍后再试");

        // 更新b树：热度值+1，库存-1
        book.setHot(book.getHot() + 1);
        book.setStock(book.getStock() - 1);

        // 更新数据库：热度值+1，库存-1
        Assert.isTrue(this.updateById(book), "服务器不稳定，请稍后再试");

        // 因为做了修改，下次查询时需重新载入
        bookListContainer.removeByBookName(book.getBookName());
        System.out.println("BTREE");
        bTree.travel();
        return true;
    }


    /**
     * 四、归还文献
     * @param bookKey 文献名转hashcode
     * @param userName 用户名
     * @return 是否成功
     */
    @Override
    public boolean returnBook(int bookKey, String userName){
        log.info("{}正在归还文献，文献号{}", userName, bookKey);
        Record record = recordService.getByBookKeyAndUserName(bookKey, userName);
        // 校验：没有借阅记录
        Assert.notNull(record, "借阅记录不存在");

        // 删除原纪录
        Assert.isTrue(recordService.deleteByBookKeyAndUserName(bookKey, userName), "服务器不稳定，请稍后再试");

        // 增加归还文献的库存量：数据库、b树
        Book book = bTree.getBook(bookKey);
        this.updateById(book.setStock(book.getStock() + 1));

        // 校验归还日期是否超过今天(校验失败仍然返回且生效)
        Assert.isTrue(record.getReturnAt().isAfter(LocalDateTime.now()), "归还超时了哦");

        // 因为做了修改，下次查询时需重新载入
        bookListContainer.removeByBookName(book.getBookName());

        return true;
    }


    /**
     * 五、获取所有文献(根据热度值进行排行)
     * @return 所有文献
     */
    @Override
    public List<Book> getAllBook() {
        log.info("正在查询所有文献");
        // 更新内存中维护的list集合，只有与btree冲突时才进行赋值操作
        bookListContainer.convertToBookList(bTree);

        return bookListContainer.sortByHotAndReturn();
    }

    /**
     * 六、获取文献的状态(关键字过滤：著者、文献名)
     * @param keyWord 关键词
     * @return 在借阅数、文献总库存、文献热度值（记得新增一下，做成排行）
     */
    @Override
    public List<BookRecordDTO> getBookStatus(String keyWord) {
        log.info("正在条件查询文献状态信息");
        List<Book> allBooks = bookListContainer.sortByHotAndReturn();

        List<Book> filteredBooks = allBooks.stream()
                .filter(book -> book.getBookName().toLowerCase().contains(keyWord.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(keyWord.toLowerCase()))
                .toList();

        // 转换为 BookRecordDTO
        return filteredBooks.stream()
                .map(book -> {
                    BookRecordDTO dto = new BookRecordDTO();
                    dto.setTotal(book.getTotal());
                    dto.setHot(book.getHot());
                    dto.setBookName(book.getBookName());
                    dto.setAuthor(book.getAuthor());
                    dto.setNumOfBorrow(book.getTotal() - book.getStock());
                    return dto;
                })
                .collect(Collectors.toList());
    }



}
