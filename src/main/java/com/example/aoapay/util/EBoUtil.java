package com.example.aoapay.util;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.table.Order;
import com.example.aoapay.table.PayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EBoUtil {

    public static String submit(Order order, PayList payList, String sLink) throws Exception {
//        try{
            Map<String, String> data = new HashMap<>();
            data.put("fxid",payList.getMchId());
            data.put("fxddh",order.getOutTradeNo());
            data.put("fxdesc","(" + order.getName()+")"+order.getUsername());
            data.put("fxfee", String.format("%.2f", order.getMoney()));
            data.put("fxnotifyurl", payList.getNotifyUrl());
            data.put("fxbackurl", payList.getCallbackUrl()+sLink);
            data.put("fxpay", payList.getTypeCode());
            data.put("fxip", order.getIp());
            data.put("fxuserid", order.getClientId());
            data.put("fxsign",  getSign(order,payList));
            String result = ToolsUtil.doPost(payList.getDomain(), data);
//            System.out.println(result);
            if (StringUtils.isEmpty(result)) throw new Exception("result null");
            JSONObject object = JSONObject.parseObject(result);
            if (object == null)throw new Exception(result);
            if (object.getInteger("status") != 1) throw new Exception(result);
            return object.getString("payurl");
//        } catch (Exception e) {
////            e.printStackTrace();
//            log.error("EBo ERROR {}", e.getMessage());
//            return null;
//        }
    }
    public static String getSign(Order order,PayList payList){
        return ToolsUtil.md5("fxid="+payList.getMchId()+"&fxddh="+order.getOutTradeNo()+"&fxfee="+String.format("%.2f", order.getMoney())+"&fxnotifyurl="+payList.getNotifyUrl()+"&"+payList.getSecretKey());
    }
}
