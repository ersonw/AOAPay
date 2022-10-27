package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.PayList;
import com.example.aoapay.table.User;
import com.example.aoapay.util.ConvertUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class UserDao extends MongoAnimal<User> {
    public UserDao() {
        super(User.class);
    }
    public boolean isAdmin(String id) {
        return super.aggregate(super.getMatch(super.and(super.or(super.where("admin",true),super.where("superAdmin",true)),super.where("_id",id)))).size() > 0;
    }
    public boolean isSuperAdmin(String id) {
        return super.aggregate(super.getMatch(super.and(super.where("superAdmin",true),super.where("_id",id)))).size() > 0;
    }
}
