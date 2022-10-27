package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.ShortLinkRecord;
import org.springframework.stereotype.Repository;

@Repository
public class ShortLinkRecordDao extends MongoAnimal<ShortLinkRecord> {
    public ShortLinkRecordDao(){
        super(ShortLinkRecord.class);
    }
}
