package com.example.aoapay.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.dao.*;
import com.example.aoapay.data.RequestHeader;
import com.example.aoapay.data.ResponseData;
import com.example.aoapay.table.Config;
import com.example.aoapay.table.Order;
import com.example.aoapay.table.PayList;
import com.example.aoapay.table.ShortLink;
import com.example.aoapay.util.DandelionUtil;
import com.example.aoapay.util.EBoUtil;
import com.example.aoapay.util.TimeUtil;
import com.example.aoapay.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ApiService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ClientDao clientDao;
    @Autowired
    private PayListDao payListDao;
    @Autowired
    private ConfigDao configDao;

    @Autowired
    private ShortLinkDao shortLinkDao;

    public static final int PAY_CHANNEL_EBO = 1;
    public static final int PAY_CHANNEL_DANDELION = 2;
    public ResponseData payList(String version, HttpServletRequest request, HttpServletResponse response) {
//        addPayList();
        Config config = configDao.findAllByVersion(version);
        if (config == null) return ResponseData.error("版本不存在！");
        if (config.isDisable()) return ResponseData.error(config.getMessage());
        List<PayList> list = payListDao.findAllByEnable();
        JSONArray array = new JSONArray();
        for (PayList payList : list) {
            JSONObject json = new JSONObject();
            json.put("id", payList.getId());
            json.put("title", payList.getTitle());
            json.put("type", payList.getType());
            json.put("amountList", payList.getAmountList());
            json.put("voluntarily", payList.isVoluntarily());
            json.put("mini", payList.getMini());
            json.put("max", payList.getMax());
            array.add(json);
        }
        return ResponseData.success(array);
    }

    private void addConfig(String version) {
        Config config = new Config();
        config.setVersion(version);
        config.setMessage("站点暂时休息，明日中午12点开始工作");
        config.setHostname("https://pay.icecology.com/");
        configDao.save(config);
    }

    private void addPayList() {
        List<Config> configs = configDao.findAll();
        PayList payList = new PayList();
        payList.setAmountList(Arrays.asList(100, 300, 500, 1000));
        payList.setAddTime(System.currentTimeMillis());
        payList.setChannel(PAY_CHANNEL_DANDELION);
        payList.setEnabled(true);
        payList.setDomain("http://107.150.125.149:19306/deposit");
        payList.setMchId("aoawin");
        payList.setSecretKey("2b2ae2e8b99ba8e68bd8d73b09eb74c2");
        payList.setType("unionpay");
        payList.setTitle("蒲公英卡对卡");
        payList.setTypeCode("5");
        payList.setLimit(0);
        payList.setMax(10000);
        payList.setMini(1);
        payList.setNotifyUrl(configs.get(0).getHostname() + "/api/notify/dandelion");
        payList.setCallbackUrl(configs.get(0).getHostname() + "/s/");
        payListDao.save(payList);
    }

    public ResponseData payListSubmit(String name, String username, String payListId, Double amount, HttpServletRequest request) {
        name = name.replaceAll(" ", "").trim();
        username = username.replaceAll(" ", "").trim();
        payListId = payListId.replaceAll(" ", "").trim();
        try {
            if (StringUtils.isEmpty(name) || StringUtils.isEmpty(username) || StringUtils.isEmpty(payListId) || amount == null)
                throw new Exception("未填写姓名或会员名");
            RequestHeader header = ToolsUtil.getRequestHeaders(request);
            if (header.getClient() == null) throw new Exception("客户端初始化失败");
            PayList payList = payListDao.findById(payListId);
            if (payList == null || !payList.isEnabled()) throw new Exception("通道已被禁用！");
            if (payList.getLimit() > 0) {
                if (orderDao.sumMoneyByPayListId(payListId, TimeUtil.getTodayZero()) >= payList.getLimit()) {
                    payList.setEnabled(false);
                    payList.setUpdateTime(System.currentTimeMillis());
                    payListDao.save(payList);
                    throw new Exception("通道已被禁用！");
                }
            }
            Order order = new Order();
            order.setAddTime(System.currentTimeMillis());
            order.setName(name);
            order.setUsername(username);
            order.setClientId(header.getClient().getId());
            order.setPayListId(payListId);
            order.setMoney(amount);
            order.setIp(header.getIp());
            order.setHeader(JSONObject.toJSONString(header));
            orderDao.save(order);
            String url = header.getSchema() + "://" + header.getServerName() + ":" + header.getServerPort() + "/payment/" + order.getOutTradeNo();
//            String url =  shortLinkService.getClient(header.getClient().getId(), "/payment/"+order.getOutOrderNo());
            return ResponseData.success(ResponseData.object("url", url));
        } catch (Exception e) {
            return ResponseData.error(e.getMessage() + ",请先刷新网页重试！");
        }
    }

    public ModelAndView payment(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);

        if (header.getClient() == null) return ToolsUtil.errorHtml("客户端初始化失败");
        Order order = orderDao.findAllByOutOrderNo(id);
        if (order == null) return ToolsUtil.errorHtml("订单号不存在!");
        if (order.isStartStatus()) return ToolsUtil.errorHtml("订单正在进行中，不可重复使用相同订单!");
        if (!order.getIp().equals(header.getIp()))
            return ToolsUtil.errorHtml("订单生成客户端与提交端不一致，请重新生成!");
        //判断订单超时
        PayList payList = payListDao.findById(order.getPayListId());
        if (payList == null) return ToolsUtil.errorHtml("通道已禁用!");
        ShortLink link = shortLinkDao.findByClient(header.getClient().getId());
        if (link == null) {
            link = new ShortLink(null, header.getClient().getId());
            shortLinkDao.save(link);
        }
        order.setStartStatus(true);
        order.setUpdateTime(System.currentTimeMillis());
        orderDao.save(order);
        try {
            return handlePayment(order, payList, link.getShortLink());
        } catch (Exception e) {
            log.error("payment ERROR {}", e.getMessage());
            orderDao.deleteById(order.getId());
            return ToolsUtil.errorHtml("渠道无法使用!");
        }
    }

    public ModelAndView handlePayment(Order order, PayList payList, String sLink) throws Exception {
        try{
            switch (payList.getChannel()) {
                case PAY_CHANNEL_EBO:
                    String payurl = EBoUtil.submit(order, payList, sLink);
                    if (payurl != null) {
                        return ToolsUtil.getHtml(payurl);
                    }
                case PAY_CHANNEL_DANDELION:
                    String pageaddress = DandelionUtil.submit(order, payList, sLink);
                    if (pageaddress != null) {
                        return ToolsUtil.getHtml(pageaddress);
                    }
            }
            throw new Exception("渠道不存在！");
        }catch (Exception e) {
//            log.error("handlePayment ERROR {}", e.getMessage());
            throw new Exception("payment "+e.getMessage());
        }
    }

    public ResponseData payListOrder(int page, HttpServletRequest request) {
//        orderDao.deleteAllByTradeStatus(false);
//        orderDao.deleteAllByTradeStatus(false,TimeUtil.getHourZero());
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getClient() == null) throw new Exception("客户端未初始化");
            page--;
            if (page < 0) page = 0;
            Page<Order> orderPage = orderDao.findAllByClient(header.getClient().getId(), page);
            JSONArray array = new JSONArray();
            for (Order order : orderPage.getContent()) {
                JSONObject json = new JSONObject();
//                json.put("id", order.getOrderNo());
                json.put("id", order.getOutTradeNo());
                json.put("amount", String.format("%.2f", order.getMoney()));
                json.put("username", order.getUsername());
                json.put("totalFee", String.format("%.2f", order.getTotalFee()));
                json.put("status", order.isStatus());
                array.add(json);
            }
            JSONObject object = ResponseData.object("list", array);
            object.put("total", orderPage.getTotalPages());
            return ResponseData.success(object);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error {}", e.getMessage());
            return ResponseData.error(e.getMessage() + ",请先刷新网页重试！");
        }
    }

    public void test() {
//        System.out.println(ToolsUtil.md5PHP("2b2ae2e8b99ba8e68bd8d73b09eb74c2aoawin20221028141850320100.00https://pay.icecology.com/api/notify/dandelion(测试)15china"));
    }
}
