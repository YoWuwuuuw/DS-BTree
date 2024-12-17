package com.toktok.btreedesign.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.toktok.btreedesign.entity.po.Book;
import com.toktok.btreedesign.entity.po.Record;
import com.toktok.btreedesign.entity.bo.RecordBo;
import com.toktok.btreedesign.mapper.BookMapper;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService, ApplicationRunner {

    /**
     * B树:存储在内存中
     */
    private static BTree bTree;

    /**
     * 用于存储所有Book对象的列表
      */
    private static List<Book> allBooks = new ArrayList<>();
    private static Set<Integer> addedIsbns = new HashSet<>();

    private void convertToBookList() {
        doConvertToBookList(bTree.root, allBooks);
    }

    private void doConvertToBookList(Node node, List<Book> books) {
        if (node == null) {
            return;
        }
        for (int i = 0; i <= node.keyNumber; i++) { // 包括最后一个孩子
            doConvertToBookList(node.children[i], books);
            if (node.keyValues[i] != null) {
                Book book = node.keyValues[i].getBook();
                Integer isbn = book.getId();
                if (!addedIsbns.contains(isbn)) { // 检查是否已添加
                    books.add(book);
                    addedIsbns.add(isbn); // 添加到已添加集合中
                }
            }
        }
    }


    /**
     * 在系统启动时加载数据库数据进入B树(内存)存储
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 采用5阶b树存储
        bTree = new BTree(5);

        // 加载进B树
        list().stream().forEach((Book book) -> {
            int hashCode = book.getBookName().hashCode();
            bTree.put(new KeyValue(hashCode, book));
        });

        // 加载进列表
        convertToBookList();
        allBooks.stream().forEach(System.out::println);
    }

    @Autowired
    private RecordService recordService;

    /**
     * 辅助方法：根据文献名获取文献
     * @param bookName 文献名
     * @return 文献实体类
     */
    private Book getBookByBookName(String bookName) {
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_name", bookName);
        return this.getOne(queryWrapper);
    }

    /**
     * 新增文献
     * @param book 实体类
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean addBook(Book book) {
        Assert.isTrue(!book.getBookName().isEmpty(), "文献不能为空");
        Book one = getBookByBookName(book.getBookName());

        // 若无记录，则新增
        if ((one == null)) {
            book.setPutInAt(LocalDateTime.now());
            this.save(book);
        } else {
            // 若有记录，则增加库存
            one.setTotal(one.getTotal() + 1);
            one.setStock(one.getStock() + 1);
            this.updateById(one);
        }

        // 操作内存：B树
        bTree.put(new KeyValue(book.getBookName().hashCode(), book));
        return true;
    }

    /**
     * 通过文献名删除文献
     * @param bookName 文献名
     * @return 是否成功
     */
    @Override
    public boolean deleteBookByBookName(String bookName) {
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_name", bookName);
        Assert.isTrue(this.remove(queryWrapper), "不存在此文献");

        bTree.remove(new KeyValue(bookName.hashCode(), new Book()));
        return true;
    }

    private boolean subBook(Book book) {
        Book one = getBookByBookName(book.getBookName());

        // 若无记录，则新增
        Assert.notNull(one, "文献不存在");

        // 若有记录，则减少库存
        one.setTotal(one.getTotal() - 1);
        one.setStock(one.getStock() - 1);
        this.updateById(one);

        // 操作内存：B树
        bTree.sub(new KeyValue(book.getBookName().hashCode(), new Book()));
        return true;
    }

    /**
     * 借阅
     * @param recordBo 借阅记录bo
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean borrowBookByBookName(RecordBo recordBo) {
        Assert.notNull(recordBo, "传入文献为空");
        int key = recordBo.getBook().getBookName().hashCode();

        Assert.isTrue(!recordService.contains(key, recordBo.getUserName()), "已借阅该文献，禁止重复借阅");

        Record record = new Record();
        // 若文献不存在，则返回false
        Assert.isTrue(bTree.contains(key), "文献不存在");
        BeanUtils.copyProperties(recordBo, record);
        // 设置借阅时间为现在
        record.setBorrowAt(LocalDateTime.now());
        record.setBookKey(key);

        // 校验库存数是否为一
        Assert.isTrue(bTree.getBook(key).getStock() > 1, "库存为1，无法借阅");

        // 在内存(B树)、数据库分别减少现库存和总库存
        Assert.isTrue(subBook(recordBo.getBook()), "服务器不稳定，请稍后再试");

        return recordService.save(record);
    }

    @Override
    public boolean returnBook(int bookKey, String userName){
        System.out.println(bookKey + "  " + userName);
        Assert.isTrue(bookKey != 0, "规范记录为空");
        Assert.isTrue(recordService.delete(bookKey, userName), "服务器不稳定，请稍后再试");

        Book book = bTree.getBook(bookKey);
        book.setStock(book.getStock() + 1);
        this.updateById(book);
        bTree.sub(new KeyValue(bookKey, null));
        return true;
    }

    @Override
    public List<Book> getAllBook() {
        // 更新内存中维护的list集合，只有与btree冲突时才进行赋值操作
        convertToBookList();
        return allBooks;
    }


}
