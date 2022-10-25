package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.PayList;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PayListDao extends MongoAnimal {
    public PayListDao(){
        super(PayList.class);
    }

    @Override
    public List<PayList> findAll() {
        List objects = super.findAll();
        List<PayList> list = new ArrayList<>();
        for(Object o : objects) {
            PayList payList = (PayList) o;
            list.add(payList);
        }
        return list;
    }
    public List<PayList> findAllByEnable(){
        List objects = super.aggregate(super.getMatch("enabled",true));
        List<PayList> list = new ArrayList<>();
        for (Object o :objects) {
            list.add((PayList) o);
        }
        return list;
    }

    @Override
    public List<PayList> findAllById(String id) {
        List objects = super.findAllById(id);
        List<PayList> list = new ArrayList<>();
        for(Object o : objects) {
            PayList payList = (PayList) o;
            list.add(payList);
        }
        return list;
    }

    @Override
    public PayList findById(String id) {
        Object o = super.findById(id);
        return (PayList)o;
    }
}
