package com.toktok.btreedesign.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="book")
@Data
@Accessors(chain=true) // 修改get、set方法返回值，实现链式调用
public class Book implements Serializable {

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

    /**
     * 删除标识：0-未删除，1-已删除
     */
    @TableField(value = "deleted")
    private Byte deleted;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return bookName.equals(book.bookName); // 根据文献名判断相等
    }

    @Override
    public int hashCode() {
        return bookName.hashCode();
    }
}
