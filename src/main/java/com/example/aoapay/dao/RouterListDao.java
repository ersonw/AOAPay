package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.RouterList;
import org.springframework.stereotype.Repository;

@Repository
public class RouterListDao extends MongoAnimal<RouterList> {
    public RouterListDao(){
        super(RouterList.class);
    }
}
