package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.LoginRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LoginRecordDao extends MongoAnimal<LoginRecord> {
    public LoginRecordDao(){
        super(LoginRecord.class);
    }

    public long getLastLoginTime(String userId) {
        List<LoginRecord> records = super.aggregate(super.getMatch(super.where("userId",userId)),super.getSort(Sort.by(Sort.Direction.DESC,"addTime")),super.getLimit(1));
        if (records.size() == 0) return 0;
        return records.get(0).getAddTime();
    }

    public long countAllByUserId(String id) {
        return super.count(super.getMatch(
                super.where("userId").is(id)
        ),super.getGroup());
    }
    public LoginRecord findByFirst(String id) {
        return super.findBy(super.getMatch(
                super.where("userId").is(id)
        ), Aggregation.sort(Sort.Direction.ASC, "addTime"),super.getLimit(1));
    }
    public LoginRecord findByLast(String id) {
        return super.findBy(super.getMatch(
                super.where("userId").is(id)
        ), Aggregation.sort(Sort.Direction.DESC, "addTime"),super.getLimit(1));
    }

    public void deleteByUserId(String userId) {
        super.remove(super.where("userId").is(userId));
    }
    public void deleteByUserIds(List<String> ids) {
        for (String id: ids) {
            deleteByUserId(id);
        }
    }
}
