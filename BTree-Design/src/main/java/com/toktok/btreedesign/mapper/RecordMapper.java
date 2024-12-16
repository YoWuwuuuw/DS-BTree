package com.toktok.btreedesign.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.toktok.btreedesign.entity.po.Record;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface RecordMapper extends BaseMapper<Record> {
}
