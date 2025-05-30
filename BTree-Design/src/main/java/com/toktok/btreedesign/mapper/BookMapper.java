package com.toktok.btreedesign.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.toktok.btreedesign.entity.po.Book;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface BookMapper extends BaseMapper<Book> {

}
