package com.example.aoapay.data;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.util.TimeUtil;
import com.example.aoapay.util.ToolsUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Setter
@Getter
public class DandelionNotify {
//    订单号
    private String orderid;
//    商户ID
    private String mid;
//    金额
    private String money;
//    类型  5 银行卡
    private String types;
//    订单备注
    private String remark;
//    2成功
    private String status;
//    订单创建时间
    private String date;
//    签名
    private String sign;
//    真实金额
    private String realmoney;
    public boolean isSign(String secretKey){
        String sSign = ToolsUtil.md5PHP(secretKey+mid+orderid+money+remark+types+status+TimeUtil.timeToStrHMS(TimeUtil.strToTime(date)));
//        System.out.println("生成sign:"+sSign);
        return Objects.equals(sSign, sign);
    }
    public static boolean isEfficient(DandelionNotify data){
        JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(data));
        for (String key: object.keySet()) {
            if (!key.equals("realmoney") && object.get(key) == null){
                return false;
            }
        }
        return true;
    }
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
