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
@Document(collection = "shortLink")
public class ShortLink {
    public ShortLink(){
        this.addTime=System.currentTimeMillis();
        this.shortLink= ToolsUtil.getRandom(7);
    }
    public ShortLink(String userId, String clientId){
        this.userId = userId;
        this.clientId = clientId;
        this.addTime=System.currentTimeMillis();
        this.shortLink= ToolsUtil.getRandom(7);
    }
    @Id
    @GeneratedValue
    private String id;
    private String userId=null;
    private String clientId=null;
    private String url=null;
    private String shortLink=null;
    private long addTime=0;
}
