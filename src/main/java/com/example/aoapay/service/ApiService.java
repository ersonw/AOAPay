package com.example.aoapay.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.dao.ClientDao;
import com.example.aoapay.dao.ConfigDao;
import com.example.aoapay.dao.OrderDao;
import com.example.aoapay.dao.PayListDao;
import com.example.aoapay.data.RequestHeader;
import com.example.aoapay.data.ResponseData;
import com.example.aoapay.table.Config;
import com.example.aoapay.table.Order;
import com.example.aoapay.table.PayList;
import com.example.aoapay.util.TimeUtil;
import com.example.aoapay.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ShortLinkService shortLinkService;

    public static final int PAY_CHANNEL_EBO = 1;
    public static final int PAY_CHANNEL_PUGONGYING = 2;
    public ResponseData payList(String version, HttpServletRequest request, HttpServletResponse response) {
        addPayList();
        Config config = configDao.findAllByVersion(version);
        if (config==null)return ResponseData.error("版本不存在！");
        if (config.isDisable()) return ResponseData.error(config.getMessage());
        List<PayList> list = payListDao.findAllByEnable();
        JSONArray array = new JSONArray();
        for (PayList payList : list) {
            JSONObject json = new JSONObject();
            json.put("id", payList.getId());
            json.put("title", payList.getTitle());
            json.put("type", payList.getType());
            json.put("amountList",payList.getAmountList());
            json.put("voluntarily", payList.isVoluntarily());
            json.put("mini", payList.getMini());
            json.put("max", payList.getMax());
            array.add(json);
        }
        return ResponseData.success(array);
    }
    private void addConfig(String version){
        Config config = new Config();
        config.setVersion(version);
        config.setMessage("站点暂时休息，明日中午12点开始工作");
        config.setHostname("https://pay.icecology.com/");
        configDao.save(config);
    }
    private void addPayList(){
        List<Config> configs = configDao.findAll();
        PayList payList = new PayList();
        payList.setAmountList(Arrays.asList(100,300,500,1000));
        payList.setAddTime(System.currentTimeMillis());
        payList.setChannel(PAY_CHANNEL_EBO);
        payList.setEnabled(true);
        payList.setDomain("https://www.ybpay88.com/Pay");
        payList.setMchId("100242");
        payList.setSecretKey("wHUMaVliOJGDHLUzAGVPhulZVRQPhzPo");
        payList.setType("wxpay");
        payList.setTitle("艺博微信话费");
        payList.setLimit(0);
        payList.setMax(10000);
        payList.setMini(1);
        payList.setNotifyUrl(configs.get(0).getHostname()+"/api/notify");
        payList.setCallbackUrl(configs.get(0).getHostname()+"/s/");
        payListDao.save(payList);
    }

    public ResponseData payListSubmit(String name, String username, String payListId, Double amount, HttpServletRequest request) {
        name=name.replaceAll(" ","").trim();
        username=username.replaceAll(" ","").trim();
        payListId=payListId.replaceAll(" ", "").trim();
        try{
            if (StringUtils.isEmpty(name)|| StringUtils.isEmpty(username)|| StringUtils.isEmpty(payListId)|| amount == null)  throw new Exception("未填写姓名或会员名");
            RequestHeader header = ToolsUtil.getRequestHeaders(request);
            if (header.getClient() == null)   throw new Exception("客户端初始化失败");
            PayList payList = payListDao.findById(payListId);
            if (payList == null || !payList.isEnabled())  throw new Exception("通道已被禁用！");
            if (payList.getLimit() > 0){
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
            orderDao.save(order);
            String url =  header.getSchema()+"://"+header.getServerName()+":"+header.getServerPort()+ "/payment/"+order.getOutOrderNo();
//            String url =  shortLinkService.getClient(header.getClient().getId(), "/payment/"+order.getOutOrderNo());
            return ResponseData.success(ResponseData.object("url",url));
        }catch (Exception e){
            return ResponseData.error(e.getMessage()+",请先刷新网页重试！");
        }
    }
}
