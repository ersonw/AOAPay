package com.example.aoapay.data;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.util.ToolsUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
public class EBoNotify {
//    商务号
    private String fxid;
//    商户订单号
    private String fxddh;
//    平台订单号
    private String fxorder;
//    商品名称
    private String fxdesc;
//    支付金额
    private String fxfee;
//    附加信息
    private String fxattch;
//    订单状态
    private String fxstatus;
//    支付时间
    private String fxtime;
//    签名
    private String fxsign;
    public boolean isSign(String secretKey){
        String sign = ToolsUtil.md5("fxstatus=" +
                fxstatus + "&fxid=" + fxid +
                "&fxddh=" + fxddh + "&fxfee=" +
                fxfee + "&" + secretKey);
        return sign.equals(fxsign);
    }
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
