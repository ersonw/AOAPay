package com.example.aoapay.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.dao.*;
import com.example.aoapay.data.RequestHeader;
import com.example.aoapay.data.ResponseData;
import com.example.aoapay.table.Order;
import com.example.aoapay.table.User;
import com.example.aoapay.util.MD5Util;
import com.example.aoapay.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private ClientDao clientDao;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private PayListDao payListDao;
    @Autowired
    private RoleListDao roleListDao;
    @Autowired
    private RolesListDao rolesListDao;
    @Autowired
    private RouterListDao routerListDao;
    @Autowired
    private ShortLinkDao shortLinkDao;
    @Autowired
    private ShortLinkRecordDao shortLinkRecordDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private ShortLinkService shortLinkService;


    public String menu(HttpServletRequest request) {
        JSONArray array = new JSONArray();
        try{
            User user = userService.auth(request);
            array = userService.getRouters(user);
        } catch (Exception e) {
            log.error("Error menu {}", e.getMessage());
        }
        return array.toJSONString();
    }

    public ResponseData login(String username, String password, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try{
            User user = userService.authUser(username,password,header);
            JSONObject object = new JSONObject();
            object.put("username", user.getUsername());
            object.put("superAdmin",user.isSuperAdmin());
            object.put("admin", user.isAdmin());
            object.put("avatar", user.getAvatar());
            object.put("token",user.getToken());
            return ResponseData.success(object);
        }catch (Exception e){
            return ResponseData.error(201,"错误："+e.getMessage());
        }
    }
    public JSONObject getOrder(Order order){
        JSONObject object = new JSONObject();
        object.put("id",order.getId());
        object.put("orderNo",order.getOrderNo());
        object.put("outTradeNo",order.getOutTradeNo());
        object.put("money",String.format("%.2f", order.getMoney()));
        object.put("name",order.getName());
        object.put("username",order.getUsername());
        object.put("totalFee",String.format("%.2f", order.getTotalFee()));
        object.put("tradeStatus",order.isTradeStatus());
        object.put("tradeNo",order.getTradeNo());
        object.put("tradeTime",order.getTradeTime());
        object.put("status",order.isStatus());
        object.put("addTime",order.getAddTime());
        object.put("updateTime",order.getUpdateTime());
        object.put("ip",order.getIp());
        object.put("link",shortLinkService.greaterLink(order.getClientId()));
        return object;
    }
    public ResponseData basicOrderList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        if(limit < 1) limit = 1;
        try{
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() || !user.isSuperAdmin()) throw new Exception("非管理员用户");
            Page<Order> orderPage;
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"addTime"));
            if (StringUtils.isNotEmpty(title)) {
                orderPage = orderDao.findAllByTitle(title,pageable);
            }else {
                orderPage = orderDao.findAll(pageable);
            }
            JSONArray array = new JSONArray();
            for (Order order: orderPage.getContent()) {
                array.add(getOrder(order));
            }
            JSONObject object = new JSONObject();
            object.put("total", orderPage.getTotalElements());
            object.put("list",array);
            return ResponseData.success(object);
        }catch (Exception e){
//            e.printStackTrace();
            return ResponseData.error(e.getMessage());
        }
    }

    public ResponseData basicOrderConfirm(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() || !user.isSuperAdmin()) throw new Exception("非管理员用户");
            Order order = orderDao.findById(id);
            if (order == null) throw new Exception("订单不存在!");
            if (order.isStatus()) throw new Exception("订单已处理过!");
            order.setStatus(true);
            order.setUpdateTime(System.currentTimeMillis());
            order.setUpdateUserId(user.getId());
            order.setUpdateUserIp(header.getIp());
            orderDao.save(order);
            return ResponseData.success("处理成功!",getOrder(order));
        }catch (Exception e){
            return ResponseData.error(e.getMessage());
        }
    }

    public ResponseData completedOrderList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        if(limit < 1) limit = 1;
        try{
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() || !user.isSuperAdmin()) throw new Exception("非管理员用户");
            Page<Order> orderPage;
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"addTime"));
            if (StringUtils.isNotEmpty(title)) {
                orderPage = orderDao.findAllByTitleCompleted(title,pageable);
            }else {
                orderPage = orderDao.findAllByCompleted(pageable);
            }
            JSONArray array = new JSONArray();
            for (Order order: orderPage.getContent()) {
                JSONObject object = getOrder(order);
                User updateUser = userDao.findById(order.getUpdateUserId());
                if (updateUser != null)object.put("updateUser", updateUser.getUsername());
                object.put("updateUserIp",order.getUpdateUserIp());
                array.add(object);
            }
            JSONObject object = new JSONObject();
            object.put("total", orderPage.getTotalElements());
            object.put("list",array);
            return ResponseData.success(object);
        }catch (Exception e){
//            e.printStackTrace();
            return ResponseData.error(e.getMessage());
        }
    }
    public ResponseData processedOrderList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        if(limit < 1) limit = 1;
        try{
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() || !user.isSuperAdmin()) throw new Exception("非管理员用户");
            Page<Order> orderPage;
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"addTime"));
            if (StringUtils.isNotEmpty(title)) {
                orderPage = orderDao.findAllByTitleProcessed(title,pageable);
            }else {
                orderPage = orderDao.findAllByProcessed(pageable);
            }
            JSONArray array = new JSONArray();
            for (Order order: orderPage.getContent()) {
                JSONObject object = getOrder(order);
                array.add(object);
            }
            JSONObject object = new JSONObject();
            object.put("total", orderPage.getTotalElements());
            object.put("list",array);
            return ResponseData.success(object);
        }catch (Exception e){
//            e.printStackTrace();
            return ResponseData.error(e.getMessage());
        }
    }
}
