package com.example.aoapay.table;

import com.example.aoapay.util.ToolsUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;

@ToString(includeFieldNames = true)
@Setter
@Getter
@Document(collection = "client")
public class Client {
    public Client(){}
    public Client(String registerHeader){
        this.registerHeader = registerHeader;
        this.updateHeader = registerHeader;
        this.id = ToolsUtil.getToken();
        this.addTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }
    @Id
    @GeneratedValue
    private String id;
    private String registerHeader=null;
    private String updateHeader=null;
    private String userId=null;
    private long addTime = System.currentTimeMillis();
    private long updateTime = System.currentTimeMillis();
}
