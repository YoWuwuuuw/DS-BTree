package com.toktok.btreedesign.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.toktok.btreedesign.entity.bo.RecordBo;
import com.toktok.btreedesign.entity.po.Record;
import com.toktok.btreedesign.mapper.RecordMapper;
import com.toktok.btreedesign.service.RecordService;
import org.springframework.stereotype.Service;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

    /**
     * 新增
     * @param entity 借阅记录
     * @return 是否成功
     */
    @Override
    public boolean save(Record entity) {
        return super.save(entity);
    }

    /**
     * 删除借阅记录(逻辑删除)【省略dao层】
     * @param bookKey 文献bookName进行hash的关键字
     * @param userName 用户名
     * @return 是否成功
     */
    @Override
    public boolean deleteByBookKeyAndUserName(int bookKey, String userName) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_key", bookKey);
        queryWrapper.eq("user_name", userName);
        return this.remove(queryWrapper);
    }

    /**
     * 查询是否存在该借阅记录【省略dao层】
     * @param bookKey 文献bookName进行hash的关键字
     * @param userName 用户名
     * @return 是否成功
     */
    @Override
    public boolean contains(int bookKey, String userName) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_key", bookKey);
        queryWrapper.eq("user_name", userName);
        return this.count(queryWrapper) > 0;
    }

    public Record getByBookKeyAndUserName(int bookKey, String userName) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_key", bookKey);
        queryWrapper.eq("user_name", userName);
        return this.getOne(queryWrapper);
    }
}