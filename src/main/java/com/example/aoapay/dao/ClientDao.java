package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Client;
import com.example.aoapay.util.ToolsUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class ClientDao extends MongoAnimal<Client> {
    public ClientDao(){
        super(Client.class);
    }

    public Page<Client> findAllByToken(String title, Pageable pageable) {
        AggregationOperation mach = super.getMatch(
                super.where("_id").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$")
        );
        List<Client> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }

    public Page<Client> findUserByUserId(String id,String title, Pageable pageable) {
        AggregationOperation mach = super.getMatch(
                super.and(
                        super.where("userId").is(id),
                        super.where("_id").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$")
                )
        );
        List<Client> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }
    public Page<Client> findAllByUserId(String id, Pageable pageable) {
        AggregationOperation mach = super.getMatch(
                super.where("userId").is(id)
        );
        List<Client> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }
}
