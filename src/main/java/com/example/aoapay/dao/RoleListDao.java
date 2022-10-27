package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.RoleList;
import org.springframework.stereotype.Repository;

@Repository
public class RoleListDao extends MongoAnimal<RoleList> {
    public RoleListDao(){
        super(RoleList.class);
    }
}
