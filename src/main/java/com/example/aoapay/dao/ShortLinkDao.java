package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.ShortLink;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShortLinkDao extends MongoAnimal<ShortLink> {
    public ShortLinkDao(){
        super(ShortLink.class);
    }
    public ShortLink findByShortLink(String id){
        List<ShortLink> list = super.aggregate(super.getMatch(super.where("shortLink").is(id)));
        if (list.size() == 0) return  null;
        return list.get(0);
    }
    public ShortLink findByClient(String id){
        List<ShortLink> list = super.aggregate(super.getMatch(super.where("clientId").regex(id)));
        if (list.size() == 0) return  null;
        return list.get(0);
    }

    public long countAllByShortLink(String shortLink) {
        return super.count(super.getMatch(
                super.where("shortLink").is(shortLink)
        ),super.getGroup());
    }

    public long countAllByInactivated(String id) {
        return super.count(super.getMatch(
                super.and(
                        super.where("userId").is(id),
                        super.where("clientId").isNull()
                )
        ),super.getGroup());
    }
}
