package com.toktok.btreedesign.service;

import com.toktok.btreedesign.entity.dto.BookRecordDTO;
import com.toktok.btreedesign.entity.po.Book;
import com.toktok.btreedesign.entity.bo.RecordBo;

import java.util.List;

public interface BookService {

    boolean addBook(Book book);

    boolean deleteBookByBookName(String bookName);

    boolean borrowBookByBookName(RecordBo recordBo);

    boolean returnBook(int bookKey, String userName);

    List<Book> getAllBook();

    List<BookRecordDTO> getBookStatus(String keyWord);

}
