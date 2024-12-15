package com.toktok.btreedesign.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName(value ="book")
@Data
public class Book {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    /**
     * 文献名
     */
    @TableField(value = "book_name")
    private String bookName;

    /**
     * 著者
     */
    @TableField(value = "author")
    private String author;

    /**
     * 现存量
     */
    @TableField(value = "stock")
    private int stock;

    /**
     * 总库存量
     */
    @TableField(value = "total")
    private int total;

    /**
     * 热度值
     */
    @TableField(value = "hot")
    private int hot;

    /**
     * 入库时间
     */
    @TableField(value = "put_in_at")
    private LocalDateTime putInAt;
}
