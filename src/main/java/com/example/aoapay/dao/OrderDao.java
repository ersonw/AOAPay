package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

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
            if (o instanceof Order)list.add((Order) o);
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
        List objects = super.aggregate(super.getMatch("outTradeNo",outOrderNo));
        if (objects.size() == 0) return null;
        Object object = objects.get(0);
        return object instanceof Order?(Order) object:null;
    }
    public Page<Order> findAllByClient(String id, int page){
        Pageable pageable = PageRequest.of(page,30, Sort.by(Sort.Direction.DESC,"addTime"));
        List<Order> list = super.aggregate(super.getMatch(super.where("clientId",id)),super.getSkip(pageable.getOffset()),super.getLimit(pageable.getPageSize()),super.getSort(pageable));
//        System.out.println(list);
        long total = super.count(super.getMatch(super.where("clientId",id)),super.getGroup());
        return new Page<Order>() {
            @Override
            public int getTotalPages() {
                if (total == 0) return 0;
                if (total < pageable.getPageSize())return 1;
                double dTotal = (total * 1D / pageable.getPageSize());
                Long sTotal = total / pageable.getPageSize();
                if (dTotal > sTotal) {
                    sTotal++;
                }
                return sTotal.intValue();
            }

            @Override
            public long getTotalElements() {
                return total;
            }

            @Override
            public <U> Page<U> map(Function<? super Order, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return pageable.getPageNumber();
            }

            @Override
            public int getSize() {
                return pageable.getPageSize();
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<Order> getContent() {
                return list;
            }

            @Override
            public boolean hasContent() {
                return list.size()>0;
            }

            @Override
            public Sort getSort() {
                return pageable.getSort();
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Order> iterator() {
                return null;
            }
        };
    }
    public void deleteAllByTradeStatus(boolean status) {
        super.remove(super.and(super.where("tradeStatus", status),super.where("status",false)));
    }
    public void deleteAllByStatus(boolean status) {
        super.remove(super.and(super.where("tradeStatus", false),super.where("status",status)));
    }
}
