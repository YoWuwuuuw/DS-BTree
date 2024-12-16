package com.toktok.btreedesign.entity.bo;

import com.toktok.btreedesign.entity.po.Book;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecordBo {

    private Book book;

    private String userName;

    // 借阅证件
    private String borrowId;

    private LocalDateTime borrowAt;

    private LocalDateTime returnAt;
}
