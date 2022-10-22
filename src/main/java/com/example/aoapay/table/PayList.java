package com.example.aoapay.table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@ToString(includeFieldNames = true)
@Setter
@Getter
@Document(collection = "payList")
public class PayList {
    @Id
    private String id;
    private String remark=null;
    @Field("amountList")
    private List<String> amountList = new ArrayList<>();
    private int floatLength = 0;
    private boolean enabledFloat = false;
    private String icon = null;
    private boolean enabled = false;
    private int max = 0;
    private int min = 0;
    private int sort = 0;
    private long limit=0L;
    private int third = 0;
    private long addTime=0L;
    private long updateTime=0L;
    private String title="";
    private String domain=null;
    private String mchId=null;
    private String callbackUrl=null;
    private String notifyUrl=null;
    private String secretKey=null;
    private String publicKey=null;
    private String rootKey=null;
    private String appPublicKey=null;
}
