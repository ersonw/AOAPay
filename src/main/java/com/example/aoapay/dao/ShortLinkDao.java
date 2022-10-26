package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Order;
import com.example.aoapay.table.ShortLink;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShortLinkDao extends MongoAnimal {
    public ShortLinkDao(){
        super(ShortLink.class);
    }

    @Override
    public ShortLink findById(String id) {
        return (ShortLink)super.findById(id);
    }
    public ShortLink findByShortLink(String id){
        List objects = super.aggregate(super.getMatch(super.where("shortLink",id)));
        if (objects.size() == 0) return  null;
        Object object = objects.get(0);
        return object instanceof ShortLink ?(ShortLink) object:null;
    }
    public ShortLink findByClient(String id){
        List objects = super.aggregate(super.getMatch(super.where("clientId",id)));
        if (objects.size() == 0) return  null;
        Object object = objects.get(0);
        return object instanceof ShortLink ?(ShortLink) object:null;
    }
}
