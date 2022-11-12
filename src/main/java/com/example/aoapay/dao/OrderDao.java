package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Order;
import com.example.aoapay.util.ToolsUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

@Repository
public class OrderDao extends MongoAnimal<Order> {
    public OrderDao(){
        super(Order.class);
    }
    public Long countAllByPayListId(String id){
        return super.count(super.getMatch("payListId",id),super.getGroup());
    }
    public Long sumMoneyByPayListId(String id){
        return super.count(super.getMatch(super.and(super.where("payListId",id),super.where("tradeStatus",true))),super.getSum("money"));
    }
    public Long sumMoneyByPayListId(String id, long start){
        return super.count(super.getMatch(super.and(super.where("payListId",id),super.where("tradeStatus",true), Criteria.where("addTime").gte(start))),super.getSum("money"));
    }
    public Long sumMoneyByPayListId(String id, long start, long end){
        return super.count(super.getMatch(super.and(super.where("payListId",id),super.where("tradeStatus",true), Criteria.where("addTime").gte(start).lte(end))),super.getSum("money"));
    }
    public Long sumTotalFeeByPayListId(String id){
        return super.count(super.getMatch("payListId",id),super.getSum("totalFee"));
    }
    public Order findAllByOutOrderNo(String outOrderNo){
        List<Order> list = super.aggregate(super.getMatch("outTradeNo",outOrderNo));
        if (list.size() == 0) return null;
        return list.get(0);
    }
    public Page<Order> findAllByClient(String id, int page){
        Pageable pageable = PageRequest.of(page,30, Sort.by(Sort.Direction.DESC,"addTime"));
        List<Order> list = super.aggregate(super.getMatch(super.where("clientId",id)),super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
//        System.out.println(list);
        long total = super.count(super.getMatch(super.where("clientId",id)),super.getGroup());
        return super.newPage(pageable, list, total);
    }
    public Page<Order> findAllByTitle(String title, Pageable pageable){
        AggregationOperation mach = super.getMatch(
                super.or(
                        super.where("orderNo").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$"),
                        super.where("outTradeNo").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$")
                )
        );
        List<Order> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }
    public Page<Order> findUserByTitle(String title,String uid, Pageable pageable){
        AggregationOperation mach = super.getMatch(
                super.and(
                        super.or(
                                super.where("orderNo").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$"),
                                super.where("outTradeNo").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$")
                        ),
                        super.where("userId").is(uid)
                )
        );
        List<Order> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }
    public Page<Order> findUser(String uid, Pageable pageable){
        AggregationOperation mach = super.getMatch(
                super.where("userId").is(uid)
        );
        List<Order> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }

    public void deleteAllByTradeStatus(boolean status) {
        super.remove(super.and(super.where("tradeStatus", status),super.where("status",false)));
    }
    public void deleteAllByTradeStatus(boolean status, long time) {
        super.remove(super.and(super.where("tradeStatus", status),super.where("status",false),super.where("addTime").lte(time)));
    }
    public void deleteAllByStatus(boolean status) {
        super.remove(super.and(super.where("tradeStatus", false),super.where("status",status)));
    }

    public Page<Order> findAllByTitleCompleted(Long start,Long end,String title, Pageable pageable) {
        List<Criteria> criteriaList =new ArrayList<>();
        criteriaList.add(super.or(
                super.where("orderNo").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$"),
                super.where("username").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$")
        ));
        List<Criteria> paramList = new ArrayList<>();
        paramList.add(super.where("status",true));
        if (start != null){
            paramList.add(super.where("updateTime").gte(start));
        }
        if (end != null){
            paramList.add(super.where("updateTime").lte(end));
        }
        Criteria[] params = new Criteria[paramList.size()];
        paramList.toArray(params);
        criteriaList.add(super.and(params));
        Criteria[] criterias = new Criteria[criteriaList.size()];
        criteriaList.toArray(criterias);
        AggregationOperation mach = super.getMatch(
                super.and(criterias)
        );
        List<Order> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }

    public Page<Order> findAllByCompleted(Long start,Long end,Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(super.where("status",true));
        if (start != null){
            criteriaList.add(super.where("updateTime").gte(start));
        }
        if (end != null){
            criteriaList.add(super.where("updateTime").lte(end));
        }
        Criteria[] params = new Criteria[criteriaList.size()];
        criteriaList.toArray(params);
        AggregationOperation mach = super.getMatch(
                super.and(params)
        );
        List<Order> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }

    public Page<Order> findAllByTitleProcessed(Long start,Long end,String title, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(super.or(
                super.where("orderNo").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$"),
                super.where("outTradeNo").regex("^.*" + ToolsUtil.escapeExprSpecialWord(title) + ".*$")
        ));
        List<Criteria> paramList = new ArrayList<>();
        paramList.add(super.where("status",false));
        paramList.add(super.where("tradeStatus",true));
        if (start != null){
            paramList.add(super.where("addTime").gte(start));
        }
        if (end != null){
            paramList.add(super.where("addTime").lte(end));
        }
        Criteria[] params = new Criteria[paramList.size()];
        paramList.toArray(params);
        criteriaList.add(super.and(params));
        Criteria[] criterias = new Criteria[criteriaList.size()];
        criteriaList.toArray(criterias);
        AggregationOperation mach = super.getMatch(
                super.and(criterias)
        );
        List<Order> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }

    public Page<Order> findAllByProcessed(Long start,Long end,Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(super.where("status",false));
        criteriaList.add(super.where("tradeStatus",true));
        if (start != null){
            criteriaList.add(super.where("addTime").gte(start));
        }
        if (end != null){
            criteriaList.add(super.where("addTime").lte(end));
        }
        Criteria[] criterias = new Criteria[criteriaList.size()];
        criteriaList.toArray(criterias);
        AggregationOperation mach = super.getMatch(
                super.and(criterias)
        );
        List<Order> list = super.aggregate(mach,super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
        long total = super.count(mach,super.getGroup());
        return super.newPage(pageable, list, total);
    }

    public long countAllByOrderNo(String orderNo) {
        return super.count(super.getMatch(
                super.where("orderNo").is(orderNo)
        ),super.getGroup());
    }
}
