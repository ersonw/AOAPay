package com.example.aoapay.config;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.table.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
@Slf4j
@Repository
public class MongoAnimal<T> {
    @Autowired
    private MongoTemplate mongoTemplate;
    private Class<T> clazz;
    private String collectionName;
    public MongoAnimal(){}
    public MongoAnimal(Class<T> clazz){
        this.clazz = clazz;
        String[] collectionNames = clazz.getCanonicalName().split("\\.");
        this.collectionName = collectionNames[collectionNames.length - 1].toLowerCase();
    }
    public MongoAnimal(Class<T> clazz, String collectionName){
        this.clazz = clazz;
        this.collectionName = collectionName;
    }
    public List<T> aggregate(Criteria criteria, Pageable pageable){
        AggregationOperation limit = Aggregation.limit(pageable.getPageSize());
        AggregationOperation skip = Aggregation.skip(pageable.getOffset());
        AggregationOperation sort = Aggregation.sort(pageable.getSort());
        AggregationOperation match = Aggregation.match(criteria);
        return aggregate(match, limit, skip,sort);
    }
    public List<T> aggregate(AggregationOperation... operations){
//        log.info("collectionName {}",this.collectionName);
        AggregationResults<T> results = mongoTemplate.aggregate(Aggregation.newAggregation(operations), collectionName, this.clazz);
        return results.getMappedResults();
    }
    private Long count(){
        AggregationResults<JSONObject> results = mongoTemplate.aggregate(Aggregation.newAggregation(group().count().as("count")), collectionName, JSONObject.class);
        if(results.getMappedResults().isEmpty()) return 0L;
        return results.getMappedResults().get(0).getLong("count");
    }
    private Long count(String regex, boolean isAnd, String... wheres){
        List<String> whereList = Arrays.asList(wheres);
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        for (String where: whereList) {
            criteriaList.add(Criteria.where(where).regex(regex));
        }
        if (isAnd) {
            criteria.andOperator(criteriaList);
        }else{
            criteria.orOperator(criteriaList);
        }
        return count(criteria);
    }
    private Long count(String regex, String... wheres){
        return count(regex,true, wheres);
    }
    private Long count(Criteria... criterias){
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = Arrays.asList(criterias);
        criteria.andOperator(criteriaList);
        AggregationOperation match = Aggregation.match(criteria);
        AggregationResults<JSONObject> results = mongoTemplate.aggregate(Aggregation.newAggregation(match, group().count().as("count")), collectionName, JSONObject.class);
        if(results.getMappedResults().isEmpty()) return 0L;
        return results.getMappedResults().get(0).getLong("count");
    }
    public List<T> findAllById(String id){
        AggregationOperation limit = Aggregation.limit(3000);
        AggregationOperation sort = Aggregation.sort(Sort.Direction.DESC, "addTime");
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("_id").is(id));
        criteria.andOperator(criteriaList);
        AggregationOperation match = Aggregation.match(criteria);
        return aggregate(match, sort,limit);
    }
    public T findById(String id){
        AggregationOperation limit = Aggregation.limit(1);
        AggregationOperation sort = Aggregation.sort(Sort.Direction.DESC, "addTime");
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("_id").is(id));
        criteria.andOperator(criteriaList);
        AggregationOperation match = Aggregation.match(criteria);
        List<T> list = aggregate(match, sort,limit);
//        System.out.println(list);
        if (list.size() == 0) return null;
        return list.get(0);
    }
    public void save(Object object) {
        mongoTemplate.save(JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(object)),this.clazz));
    }
    public void save(List<Object> objects) {
        for (Object object: objects) {
            mongoTemplate.remove(JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(object)),this.clazz));
        }
    }
    public void delete(Object object) {
        mongoTemplate.remove(JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(object)),this.clazz));
    }
    public void delete(List<Object> objects) {
        for (Object object: objects) {
            mongoTemplate.remove(JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(object)),this.clazz));
        }
    }
}
