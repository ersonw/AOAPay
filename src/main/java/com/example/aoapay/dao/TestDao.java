package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Test;
import org.springframework.stereotype.Repository;

@Repository
public class TestDao extends MongoAnimal<Test> {
    public TestDao(){
        super(Test.class);
    }
}
