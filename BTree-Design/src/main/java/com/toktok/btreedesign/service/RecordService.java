package com.toktok.btreedesign.service;

import com.toktok.btreedesign.entity.po.Record;

public interface RecordService {
    boolean save(Record record);

    boolean delete(int bookKey, String userName);

    boolean contains(int bookKey, String userName);
}
