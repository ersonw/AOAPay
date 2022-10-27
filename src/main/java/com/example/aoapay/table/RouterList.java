package com.example.aoapay.table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;

@ToString(includeFieldNames = true)
@Setter
@Getter
@Document(collection = "routerList")
public class RouterList {
    @Id
    @GeneratedValue
    private String id;
    private String superior=null;
    private String key=null;
    private String title=null;
    private String icon=null;
    private String component=null;
    private long addTime = System.currentTimeMillis();
    private long updateTime = System.currentTimeMillis();
}
