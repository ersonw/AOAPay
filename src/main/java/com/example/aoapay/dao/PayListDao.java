package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Config;
import com.example.aoapay.table.PayList;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PayListDao extends MongoAnimal<PayList> {
    public PayListDao(){
        super(PayList.class);
    }
    public List<PayList> findAllByEnable(){
        return super.aggregate(super.getMatch("enabled",true));
    }
}
