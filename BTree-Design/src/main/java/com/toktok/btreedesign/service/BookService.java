package com.toktok.btreedesign.service;

import com.toktok.btreedesign.entity.po.Book;
import com.toktok.btreedesign.entity.bo.RecordBo;

public interface BookService {

    boolean addBook(Book book);

    boolean deleteBookByBookName(String bookName);

    boolean borrowBookByBookName(RecordBo recordBo);

    boolean returnBook(int bookKey, String userName);

}
