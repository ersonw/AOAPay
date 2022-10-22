package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends MongoAnimal {
    public UserDao() {
        super(UserDao.class);
    }
}
