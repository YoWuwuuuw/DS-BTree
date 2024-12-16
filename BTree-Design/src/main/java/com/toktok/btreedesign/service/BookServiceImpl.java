package com.toktok.btreedesign.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.toktok.btreedesign.entity.po.Book;
import com.toktok.btreedesign.entity.po.Record;
import com.toktok.btreedesign.entity.bo.RecordBo;
import com.toktok.btreedesign.mapper.BookMapper;
import com.toktok.btreedesign.utils.BTree;
import com.toktok.btreedesign.utils.KeyValue;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService, ApplicationRunner {

    /**
     * B树:存储在内存中
     */
    private static BTree bTree;

    @Autowired
    private RecordService recordService;

    /**
     * 在系统启动时加载数据库数据进入B树(内存)存储
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 采用5阶b树存储
        bTree = new BTree(5);

        list().stream().forEach((Book book) -> {
            int hashCode = book.getBookName().hashCode();
            bTree.put(new KeyValue(hashCode, book));
        });

//        测试hashcode能否成功
//        String s = new String("思考，快与慢");
//        int key = s.hashCode();
//        bTree.remove(new KeyValue(key, new Book()));
//
//        bTree.travel();
    }

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
        Book one = getBookByBookName(book.getBookName());

        // 若无记录，则新增
        if ((one == null)) {
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
        bTree.put(new KeyValue(book.getBookName().hashCode(), one));
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
        Assert.isTrue(recordBo.getBook().getStock() > 1, "库存为1，无法借阅");

        // 在内存(B树)、数据库分别减少现库存和总库存
        Assert.isTrue(subBook(recordBo.getBook()), "服务器不稳定，请稍后再试");

        return recordService.save(record);
    }

    @Override
    public boolean returnBook(int bookKey, String userName){
        Assert.isTrue(recordService.delete(bookKey, userName), "服务器不稳定，请稍后再试");
        return true;
    }



}
