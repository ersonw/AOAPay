package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.ShortLink;
import org.springframework.stereotype.Repository;

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
        return (ShortLink)super.aggregate(super.getMatch(super.where("shortLink",id)));
    }
}
