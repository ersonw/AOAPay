package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.User;
import com.example.aoapay.util.ConvertUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class UserDao extends MongoAnimal {
    public UserDao() {
        super(UserDao.class);
    }
    public boolean isAdmin(String id) {
        return super.aggregate(super.getMatch(super.and(super.or(super.where("admin",true),super.where("superAdmin",true)),super.where("_id",id)))) != null;
    }
    public boolean isSuperAdmin(String id) {
        return super.aggregate(super.getMatch(super.and(super.where("superAdmin",true),super.where("_id",id)))) != null;
    }
    @Override
    public List findAll() {
        List objects = super.findAll();
        List<User> list = new ArrayList<>();
        for(Object o : objects) {
            list.add((User) o);
        }
        return list;
    }

    @Override
    public List<User> findAllById(String id) {
        List objects = super.findAllById(id);
        List<User> list = new ArrayList<>();
        for(Object o : objects) {
            list.add((User) o);
        }
        return list;
    }

    @Override
    public User findById(String id) {
        return (User)super.findById(id);
    }
}