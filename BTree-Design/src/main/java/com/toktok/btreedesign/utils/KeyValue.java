package com.toktok.btreedesign.utils;

import com.toktok.btreedesign.entity.Book;
import lombok.Data;

@Data
public class KeyValue {

    /**
     * 关键字
     */
    private int key;

    /**
     * 文献实体类
     */
    private Book book;

    public KeyValue() {}

    public KeyValue(int key, Book book) {
        this.key = key;
        this.book = book;
    }
}
