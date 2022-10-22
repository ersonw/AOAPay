package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.PayList;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PayListDao extends MongoAnimal {
    public PayListDao(){
        super(PayList.class);
    }
}
