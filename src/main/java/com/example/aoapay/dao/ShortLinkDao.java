package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.ShortLink;
import com.example.aoapay.util.ToolsUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
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

    public Page<ShortLink> findAllByTitle(String title, Pageable pageable) {
        AggregationOperation mach = super.getMatch(
                super.where("shortLink").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$")
        );
        List<ShortLink> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }

    public Page<ShortLink> findTitleByUserId(String id, String title, Pageable pageable) {
        AggregationOperation mach = super.getMatch(
                super.and(
                        super.where("shortLink").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$"),
                        super.where("userId").is(id)
                )
        );
        List<ShortLink> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }

    public Page<ShortLink> findAllByUserId(String id, Pageable pageable) {
        AggregationOperation mach = super.getMatch(
                super.where("userId").is(id)
        );
        List<ShortLink> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }

    public void deleteByUserId(String userId) {
        super.remove(super.where("userId").is(userId));
    }
    public void deleteByUserIds(List<String> ids) {
        for (String id: ids) {
            deleteByUserId(id);
        }
    }
}
