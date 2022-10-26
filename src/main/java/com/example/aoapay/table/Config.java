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
@Document(collection = "config")
public class Config {
    public Config(){
        this.addTime=System.currentTimeMillis();
        this.updateTime=System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private String id;
    private String version=null;
    private boolean disable=false;
    private String message=null;
    private String hostname=null;
    private long timeout=0;
    private String addUserId=null;
    private String updateUserId=null;
    private long addTime=0;
    private long updateTime=0;
}
