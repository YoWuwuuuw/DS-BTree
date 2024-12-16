package com.toktok.btreedesign.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="record")
@Data
public class Record implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @TableField(value = "book_key")
    private int bookKey;

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "borrow_id")
    private String borrowId;

    @TableField(value = "borrow_at")
    private LocalDateTime borrowAt;

    @TableField(value = "return_at")
    private LocalDateTime returnAt;

    /**
     * 删除标识：0-未删除，1-已删除
     */
    @TableField(value = "deleted")
    private Byte deleted;
}
