package com.toktok.btreedesign.service;

import com.toktok.btreedesign.entity.po.Record;

public interface RecordService {

    boolean save(Record record);

    boolean deleteByBookKeyAndUserName(int bookKey, String userName);

    boolean contains(int bookKey, String userName);

    Record getByBookKeyAndUserName(int bookName, String userName);
}
