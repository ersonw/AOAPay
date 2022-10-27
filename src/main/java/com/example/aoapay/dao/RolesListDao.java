package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.RolesList;
import org.springframework.stereotype.Repository;

@Repository
public class RolesListDao extends MongoAnimal<RolesList> {
    public RolesListDao(){
        super(RolesList.class);
    }
}
