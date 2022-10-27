package com.example.aoapay.util;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.table.Order;
import com.example.aoapay.table.PayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DandelionUtil {
    public static String submit(Order order, PayList payList, String sLink) throws Exception {
//        try{
        Map<String, String> data = new HashMap<>();
        data.put("ip", order.getIp());
        data.put("orderid",order.getOutTradeNo());
        data.put("mname","aoawin");
        data.put("mid",payList.getMchId());
        data.put("money", String.format("%.2f", order.getMoney()));
        data.put("types", payList.getTypeCode());
        data.put("returnurl", payList.getNotifyUrl());
        data.put("remark","(" + order.getName()+")"+order.getUsername());
//        data.put("numf", "1234");
        data.put("country", "china");
        data.put("sname",order.getClientId());
        data.put("sign",  getSign(data,payList));
        String result = ToolsUtil.doPost(payList.getDomain(), data);
//            System.out.println(result);
        if (StringUtils.isEmpty(result)) throw new Exception("result null");
        JSONObject object = JSONObject.parseObject(result);
        if (object == null)throw new Exception(result);
        if (object.getInteger("status") != 1) throw new Exception(result);
        log.info("蒲公英返回 {}",object.get("data"));
        return object.getString("pageaddress");
//        } catch (Exception e) {
////            e.printStackTrace();
//            log.error("EBo ERROR {}", e.getMessage());
//            return null;
//        }
    }
    public static String getSign(Map<String, String> data, PayList payList){
        return ToolsUtil.md5(payList.getSecretKey()+payList.getMchId()+data.get("orderid")+payList.getNotifyUrl()+data.get("remark")+data.get("types")+data.get("country"));
    }
}
