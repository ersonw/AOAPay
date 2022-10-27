package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Config;
import com.example.aoapay.table.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ConfigDao extends MongoAnimal<Config> {
    public ConfigDao(){
        super(Config.class);
    }
    public Config findAllByVersion(String version){
        List<Config> list = super.aggregate(super.getMatch("version",version));
        if (list.size() == 0) return null;
        return list.get(0);
    }
}
