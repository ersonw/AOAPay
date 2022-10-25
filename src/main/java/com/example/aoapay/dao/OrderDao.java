package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDao extends MongoAnimal {
    public OrderDao(){
        super(Order.class);
    }

    @Override
    public Order findById(String id) {
        return (Order)super.findById(id);
    }

    @Override
    public List findAllById(String id) {
        List objects = super.findAllById(id);
        List<Order> list = new ArrayList<>();
        for(Object o : objects) {
            list.add((Order) o);
        }
        return list;
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
        List objects = super.aggregate(super.getMatch("outOrderNo",outOrderNo));
        return (Order) objects.get(0);
    }
}
