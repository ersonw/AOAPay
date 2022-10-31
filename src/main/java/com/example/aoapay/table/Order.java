package com.example.aoapay.table;

import com.example.aoapay.util.TimeUtil;
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
@Document(collection = "order")
public class Order {
    public Order(){
        this.addTime = System.currentTimeMillis();
        this.orderNo = ToolsUtil.getRandom(7).toUpperCase();
        this.outTradeNo = TimeUtil._getOrderNo();
    }
    @Id
    @GeneratedValue
    private String id;
    private String clientId=null;
    private String ip=null;
    private String header=null;
    private String payListId=null;
    private String outTradeNo=null;
    private String tradeNo=null;
    private String orderNo=null;
    private double money=0;
    private String name=null;
    private String username=null;
    private double totalFee=0;
    private boolean tradeStatus=false;
    private boolean startStatus=false;
    private boolean status=false;
    private String updateUserId=null;
    private String updateUserIp=null;
    private long addTime=0;
    private long updateTime=0;
    private long noticeTime=0;
    private long tradeTime=0;
}
