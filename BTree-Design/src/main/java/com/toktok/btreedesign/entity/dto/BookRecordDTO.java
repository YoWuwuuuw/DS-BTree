package com.toktok.btreedesign.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookRecordDTO {
    String bookName;

    String author;

    /**
     * 正在借阅的人数
     */
    int numOfBorrow;

    int total;

    int hot;
}
