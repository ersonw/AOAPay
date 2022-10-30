package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.LoginRecordFail;
import org.springframework.stereotype.Repository;

@Repository
public class LoginRecordFailDao extends MongoAnimal<LoginRecordFail> {
    public LoginRecordFailDao(){
        super(LoginRecordFail.class);
    }

    public long countAllByUserId(String userId, long time) {
        return super.count(super.getMatch(super.and(super.where("userId",userId),super.where("addTime").gte(time))),super.getGroup());
    }
}
