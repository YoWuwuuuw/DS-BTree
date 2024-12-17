package com.toktok.btreedesign.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.toktok.btreedesign.entity.po.Book;
import com.toktok.btreedesign.entity.bo.RecordBo;

import java.util.List;

public interface BookService {

    boolean addBook(Book book);

    boolean deleteBookByBookName(String bookName);

    boolean borrowBookByBookName(RecordBo recordBo);

    boolean returnBook(int bookKey, String userName);

    List<Book> getAllBook();

}
