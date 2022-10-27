package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Order;
import com.example.aoapay.table.ShortLink;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShortLinkDao extends MongoAnimal<ShortLink> {
    public ShortLinkDao(){
        super(ShortLink.class);
    }
    public ShortLink findByShortLink(String id){
        List<ShortLink> list = super.aggregate(super.getMatch(super.where("shortLink",id)));
        if (list.size() == 0) return  null;
        return list.get(0);
    }
    public ShortLink findByClient(String id){
        List<ShortLink> list = super.aggregate(super.getMatch(super.where("clientId",id)));
        if (list.size() == 0) return  null;
        return list.get(0);
    }
}
