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
@Document(collection = "shortLinkRecord")
public class ShortLinkRecord {
    public ShortLinkRecord(){
    }
    public ShortLinkRecord(String shortLinkId, String header){
        this.shortLinkId=shortLinkId;
        this.header=header;
    }
    @Id
    @GeneratedValue
    private String id;
    private String shortLinkId=null;
    private String header=null;
    private long addTime=System.currentTimeMillis();
}
