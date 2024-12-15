package com.toktok.btreedesign.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.toktok.btreedesign.entity.Book;
import com.toktok.btreedesign.mapper.BookMapper;
import com.toktok.btreedesign.utils.BTree;
import com.toktok.btreedesign.utils.KeyValue;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService, ApplicationRunner {

    /**
     * B树
     */
    private static BTree bTree;

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

        bTree.travel();
    }



}
