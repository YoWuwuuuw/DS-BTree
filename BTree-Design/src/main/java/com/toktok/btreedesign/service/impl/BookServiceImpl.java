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
import com.toktok.btreedesign.utils.KeyValue;
import com.toktok.btreedesign.utils.Node;
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

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService, ApplicationRunner {

    /**
     * 1. B树:存储文献，存储在内存中
     */
    private static BTree bTree;

    /**
     * 2. Book的List集合：用于存储所有Book对象的列表
     * 3. Book的HashSet集合，用于过滤已存在的Book，提高add等操作cpu
      */
    private static List<Book> allBooks = new ArrayList<>();
    private static Set<Integer> addedIsbns = new HashSet<>();

    /**
     * 辅助方法：用于在系统启动时，将B树所有数据copy到List、HashSet中存储
     */
    private void convertToBookList() {
        doConvertToBookList(bTree.root, allBooks);
        removeBooksNotInBTree(allBooks);
    }

    private void doConvertToBookList(Node node, List<Book> books) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < node.keyNumber; i++) { // 注意这里的循环条件
            doConvertToBookList(node.children[i], books);
            if (node.keyValues[i] != null) {
                Book book = node.keyValues[i].getBook();
                Integer isbn = book.getBookName().hashCode();
                if (!addedIsbns.contains(isbn)) { // 检查是否已添加
                    // 删除可能已有的缓存
                    books.removeIf(b -> b.getBookName().equals(book.getBookName()));
                    books.add(book);
                    addedIsbns.add(isbn); // 添加到已添加集合中
                }
            }
        }
        doConvertToBookList(node.children[node.keyNumber], books); // 处理最后一个孩子
    }

    /**
     * 辅助方法：移除在B树中不存在的书籍
     */
    private void removeBooksNotInBTree(List<Book> books) {
        books.removeIf(book -> !bTree.contains(book.getBookName().hashCode()));
    }


    /**
     * 系统启动线程：在系统启动时加载数据库数据进入B树(内存)存储
     */
    @Override
    public void run(ApplicationArguments args) {
        // 采用5阶b树存储
        bTree = new BTree(5);

        // 数据库数据加载进B树
        list().stream().forEach((Book book) -> {
            int hashCode = book.getBookName().hashCode();
            bTree.put(new KeyValue(hashCode, book));
        });

        // B树数据加载进List、HashSet
        convertToBookList();
//        allBooks.stream().forEach(System.out::println);
    }


    @Autowired
    private RecordService recordService;

    /**
     * 辅助方法：根据文献名获取文献【省略dao层】
     * @param bookName 文献名
     * @return 文献实体类
     */
    private Book getBookByBookName(String bookName) {
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_name", bookName);
        return this.getOne(queryWrapper);
    }


    /**
     * 一、新增文献
     * @param book 实体类
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean addBook(Book book) {
        Assert.isTrue(book.getBookName().isEmpty(), "传入文献不能为空");
        Assert.isTrue(book.getTotal() > 0, "库存量必须为正数");
        // B树中查询是否存在
        boolean isTrue = bTree.contains(book.getBookName().hashCode());

        // 若无记录，则新增
        if (!isTrue) {
            // 新增 book
            this.save(book.setPutInAt(LocalDateTime.now()).setTotal(book.getStock()));

            // 更新内存：B树
            bTree.put(new KeyValue(book.getBookName().hashCode(), book));
        } else {
            Book one = this.getOne(new QueryWrapper<Book>().eq("book_name", book.getBookName()));
            one.setTotal(one.getTotal() + book.getStock());
            one.setStock(one.getStock() + book.getStock());
            // 若有记录，则增加库存量、总库存量(根据新增文献批次的total、stock进行累加)
            this.updateById(one);

            // 更新内存：B树
            bTree.put(new KeyValue(one.getBookName().hashCode(), one.setStock(book.getStock())));

            // 表示此书已被修改，应重新导入list
            addedIsbns.remove(book.getBookName().hashCode());
        }
        return true;
    }


    /**
     * 二、通过文献名删除文献【省略dao层】
     * @param bookName 文献名
     * @return 是否成功
     */
    @Override
    public boolean deleteBookByBookName(String bookName) {
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_name", bookName);
        // 若删除文献失效，则提示不存在文献
        Assert.isTrue(this.remove(queryWrapper), "不存在此文献");
        System.out.println(" *** "+bookName.hashCode());
        // 更新内存(Book传入null，因为用不到)
        bTree.remove(new KeyValue(bookName.hashCode(), null));

        addedIsbns.remove(bookName.hashCode());
        return true;
    }

    /**
     * 辅助方法：用于减少该Book的数据库、内存的库存量
     * @param book 实体类
     * @return 是否成功
     */
    private boolean subBook(Book book) {
        // 拿到文献的数据库数据
        Book one = getBookByBookName(book.getBookName());

        // 若有记录，则减少库存
        this.updateById(one.setStock(one.getStock() - 1).setHot(one.getHot() + 1));

        // 更新内存：B树
        bTree.sub(new KeyValue(book.getBookName().hashCode(), null));
        return true;
    }

    private boolean update(Book book) {
        return this.updateById(book);
    }


    /**
     * 三、借阅文献
     * @param recordBo 借阅记录bo
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean borrowBookByBookName(RecordBo recordBo) {
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

        // 更新内存：B树
        Assert.isTrue(this.update(book.setHot(book.getHot() + 1)), "服务器不稳定，请稍后再试");
        Assert.isTrue(subBook(recordBo.getBook()), "服务器不稳定，请稍后再试");
        Assert.isTrue(recordService.save(record), "服务器不稳定，请稍后再试");

        addedIsbns.remove(book.getBookName().hashCode());
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
        Record record = recordService.getByBookKeyAndUserName(bookKey, userName);

        // 删除原纪录
        Assert.isTrue(recordService.deleteByBookKeyAndUserName(bookKey, userName), "服务器不稳定，请稍后再试");

        // 增加归还文献的库存量
        Book book = bTree.getBook(bookKey);
        this.updateById(book.setStock(book.getStock() + 1));

        // 更新内存：B树
        bTree.add(new KeyValue(bookKey, null));

        // 校验归还日期是否超过今天(校验失败仍然返回且生效)
        Assert.isTrue(record.getReturnAt().isAfter(LocalDateTime.now()), "归还超时了哦");
        addedIsbns.remove(book.getBookName().hashCode());
        return true;
    }


    /**
     * 五、获取所有文献(根据热度值进行排行)
     * @return 所有文献
     */
    @Override
    public List<Book> getAllBook() {
        // 更新内存中维护的list集合，只有与btree冲突时才进行赋值操作
        convertToBookList();

        // 按hot降序排序：对于重复的调用，sort api内部交换值的次数很少，个别情况O(n) = n
        allBooks.sort(Comparator.comparingInt(Book::getHot).reversed());

        return allBooks;
    }

    /**
     * 六、获取文献的状态(关键字过滤：著者、文献名)
     * @param keyWord 关键词
     * @return 在借阅数、文献总库存、文献热度值（记得新增一下，做成排行）
     */
    @Override
    public List<BookRecordDTO> getBookStatus(String keyWord) {
        List<Book> filteredBooks = allBooks.stream()
                .filter(book -> book.getBookName().toLowerCase().contains(keyWord.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(keyWord.toLowerCase()))
                .toList();

        // 转换为 BookRecordDTO
        List<BookRecordDTO> bookRecordDTOS = filteredBooks.stream()
                .map(book -> {
                    BookRecordDTO dto = new BookRecordDTO();
                    dto.setTotal(book.getTotal());
                    dto.setHot(book.getHot());
                    dto.setBookName(book.getBookName());
                    dto.setAuthor(book.getAuthor());
                    dto.setNumOfBorrow(book.getTotal() - book.getStock()); // 计算 numOfBorrow
                    // 设置其他属性，如 hot
                    return dto;
                })
                .collect(Collectors.toList());

        return bookRecordDTOS;
    }



}
