package com.toktok.btreedesign.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.toktok.btreedesign.entity.po.Record;
import com.toktok.btreedesign.mapper.RecordMapper;
import org.springframework.stereotype.Service;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

    @Override
    public boolean save(Record entity) {
        return super.save(entity);
    }

    @Override
    public boolean delete(int bookKey, String userName) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_key", bookKey);
        queryWrapper.eq("user_name", userName);
        return this.remove(queryWrapper);
    }

    @Override
    public boolean contains(int bookKey, String userName) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_key", bookKey);
        queryWrapper.eq("user_name", userName);
        return this.count(queryWrapper) > 0;
    }
}