package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.RouterList;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RouterListDao extends MongoAnimal<RouterList> {
    public RouterListDao(){
        super(RouterList.class);
    }
    public List<RouterList> findAllByFirst(){
        return super.aggregate(super.getMatch(super.where("superior",null)));
    }
    public List<RouterList> findAllByFirst(String id){
        return super.aggregate(super.getMatch(super.where("superior",id)));
    }
    public List<RouterList> findAllBySub(){
        return super.aggregate(super.getMatch(super.where("superior").ne(null)));
    }
}
