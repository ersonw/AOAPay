package com.example.aoapay.table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.GeneratedValue;
import java.util.ArrayList;
import java.util.List;

@ToString(includeFieldNames = true)
@Setter
@Getter
@Document(collection = "payList")
public class PayList {
    @Id
    @GeneratedValue
    private String id;
    private String remark=null;
    private String type = null;
    private String typeCode = null;
    private String title="";
    private String domain=null;
    private String mchId=null;
    private String callbackUrl=null;
    private String notifyUrl=null;
    private String secretKey=null;
    private String publicKey=null;
    private List<String> callbackIp=new ArrayList<>();
    private boolean voluntarily = false;
    private boolean enabled = false;
    @Field("amountList")
    private List<Integer> amountList = new ArrayList<>();
    private int channel = 0;
    private int max = 0;
    private int mini = 0;
    private int sort = 0;
    private long limit=0L;
    private long addTime=0L;
    private long updateTime=0L;
}
