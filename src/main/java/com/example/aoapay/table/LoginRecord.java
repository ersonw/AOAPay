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
@Document(collection = "loginRecord")
public class LoginRecord {
    public LoginRecord(){}
    public LoginRecord(String userId, String ip, String header){
        this.userId = userId;
        this.header = header;
        this.ip = ip;
    }
    @Id
    @GeneratedValue
    private String id;
    private String userId=null;
    private String ip=null;
    private String header=null;
    private long addTime=System.currentTimeMillis();
}
