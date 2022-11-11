package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.ShortLinkRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

@Repository
public class ShortLinkRecordDao extends MongoAnimal<ShortLinkRecord> {
    public ShortLinkRecordDao(){
        super(ShortLinkRecord.class);
    }

    public ShortLinkRecord findByFirst(String id) {
        return super.findBy(super.getMatch(
                super.where("shortLinkId").is(id)
        ), Aggregation.sort(Sort.Direction.ASC, "addTime"),super.getLimit(1));
    }
    public ShortLinkRecord findByLast(String id) {
        return super.findBy(super.getMatch(
                super.where("shortLinkId").is(id)
        ), Aggregation.sort(Sort.Direction.DESC, "addTime"),super.getLimit(1));
    }

    public long countAllById(String id) {
        return super.count(super.getMatch(
                super.where("shortLinkId").is(id)
        ),super.getGroup());
    }
}
