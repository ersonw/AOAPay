package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Config;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ConfigDao extends MongoAnimal {
    public ConfigDao(){
        super(Config.class);
    }
    public Config findAllByVersion(String version){
        List objects = super.aggregate(super.getMatch("version",version));
        if (objects.size() > 0) return (Config)objects.get(0);
        return null;
    }

    @Override
    public List<Config> findAll() {
        List objects = super.findAll();
        List<Config> list = new ArrayList<>();
        for (Object o : objects) {
            list.add((Config) o);
        }
        return list;
    }
}
