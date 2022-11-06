package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Config;
import com.example.aoapay.table.Order;
import com.example.aoapay.table.PayList;
import com.example.aoapay.util.ToolsUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PayListDao extends MongoAnimal<PayList> {
    public PayListDao(){
        super(PayList.class);
    }
    public List<PayList> findAllByEnable(){
        return super.aggregate(super.getMatch("enabled",true),super.getSort(Sort.Direction.DESC,"sort"));
    }

    public Page<PayList> findAllByTitle(String title, Pageable pageable) {
        AggregationOperation mach = super.getMatch(
                super.or(
                        super.where("title").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$"),
                        super.where("type").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$")
                )
        );
        List<PayList> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }
}
