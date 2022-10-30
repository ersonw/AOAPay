package com.example.aoapay.service;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.dao.OrderDao;
import com.example.aoapay.dao.PayListDao;
import com.example.aoapay.data.DandelionNotify;
import com.example.aoapay.data.EBoNotify;
import com.example.aoapay.data.RequestHeader;
import com.example.aoapay.table.Order;
import com.example.aoapay.table.PayList;
import com.example.aoapay.util.TimeUtil;
import com.example.aoapay.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Slf4j
public class NotifyService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private PayListDao payListDao;
    public String eBo(EBoNotify data, HttpServletRequest request) {
//        log.info("艺博支付回调 {}",JSONObject.toJSONString(data));
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        if (StringUtils.isEmpty(data.getFxstatus())
                || StringUtils.isEmpty(data.getFxid())
                || StringUtils.isEmpty(data.getFxddh())
                || StringUtils.isEmpty(data.getFxfee())
        ) return "error";
        Order order = orderDao.findAllByOutOrderNo(data.getFxddh());
        if (order == null) return "error";
        PayList payList = payListDao.findById(order.getPayListId());
        if (payList == null) return "error";
        if (!isWhiteIP(payList.getCallbackIp(),header.getIp())) return "error";
        if (!data.isSign(payList.getSecretKey())) return "error";
        if (data.getFxstatus().equals("1") && !order.isTradeStatus()){
            order.setTradeStatus(true);
            order.setTradeNo(data.getFxorder());
            order.setTotalFee(new Double(data.getFxfee()));
            order.setTradeTime(new Long(data.getFxtime()) * 1000);
            orderDao.save(order);
        }
        return "success";
    }
    public boolean isWhiteIP(List<String> ips, String ip){
        if (ips.isEmpty()) return true;
        for (String i: ips) {
            if (ip.equals(i)){
                return true;
            }
        }
        return false;
    }

    public String dandelion(DandelionNotify data, HttpServletRequest request) {
//        log.info("蒲公英支付回调 {}",JSONObject.toJSONString(data));
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        if (!DandelionNotify.isEfficient(data))  return "error";
        Order order = orderDao.findAllByOutOrderNo(data.getOrderid());
        if (order == null) return "error";
        PayList payList = payListDao.findById(order.getPayListId());
        if (payList == null) return "error";
        if (!isWhiteIP(payList.getCallbackIp(),header.getIp())) return "error";
        if (!data.isSign(payList.getSecretKey())) {
            log.info("蒲公英验签失败 回调sign={}",data.getSign());
            return "error";
        }
        if (data.getStatus().equals("2") && !order.isTradeStatus()){
            order.setTradeStatus(true);
            if (StringUtils.isNotEmpty(data.getRealmoney()))order.setTotalFee(new Double(data.getRealmoney()));
            order.setTradeTime(TimeUtil.strToTime(data.getDate()));
            orderDao.save(order);
        }
        return "ok";
    }
}
