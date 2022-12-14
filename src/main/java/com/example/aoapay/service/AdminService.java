package com.example.aoapay.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.dao.*;
import com.example.aoapay.data.RequestHeader;
import com.example.aoapay.data.ResponseData;
import com.example.aoapay.table.*;
import com.example.aoapay.util.MD5Util;
import com.example.aoapay.util.RoleUtil;
import com.example.aoapay.util.TimeUtil;
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
import java.util.List;

@Slf4j
@Service
public class AdminService {

    private static final int SHORT_LINK_MAX = 5;
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
    private ShortLinkService shortLinkService;
    @Autowired
    private UserService userService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private LoginRecordDao loginRecordDao;
    @Autowired
    private LoginRecordFailDao loginRecordFailDao;


    public ResponseData menu(HttpServletRequest request) {

        try{
            JSONArray array = new JSONArray();
            User user = userService.auth(request);
            array = userService.getRouters(user);
            return ResponseData.success(array);
        } catch (Exception e) {
            log.error("Error menu {}", e.getMessage());
            return ResponseData.error(201,"?????????"+e.getMessage());
        }
    }

    public ResponseData login(String username, String password, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try{
            User user = userService.authUser(username,password,header);
            return ResponseData.success(getUserInfo(user));
        }catch (Exception e){
            return ResponseData.error(201,"?????????"+e.getMessage());
        }
    }
    public JSONObject getOrder(Order order){
        JSONObject object = new JSONObject();
        object.put("channel","???????????????");
        if(StringUtils.isNotEmpty(order.getPayListId())){
            PayList list = payListDao.findById(order.getPayListId());
            if (list != null){
                object.put("channel",list.getTitle());
            }
        }
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
        object.put("link",shortLinkService.greaterLink(order.getClientId(),order.getUserId()));
        return object;
    }
    public ResponseData basicOrderList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        if(limit < 1) limit = 1;
        try{
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
//            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            Page<Order> orderPage;
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"addTime"));
            if (StringUtils.isNotEmpty(title)) {
                if (user.isAdmin() || user.isSuperAdmin()){
                    orderPage = orderDao.findAllByTitle(title,pageable);
                }else{
                    orderPage = orderDao.findUserByTitle(title,user.getId(),pageable);
                }
            }else {
                if (user.isAdmin() || user.isSuperAdmin()){
                    orderPage = orderDao.findAll(pageable);
                }else{
                    orderPage = orderDao.findUser(user.getId(),pageable);
                }
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
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData basicOrderConfirm(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            Order order = orderDao.findById(id);
            if (order == null) throw new Exception("???????????????!");
            if (order.isStatus()) throw new Exception("??????????????????!");
            order.setStatus(true);
            order.setUpdateTime(System.currentTimeMillis());
            order.setUpdateUserId(user.getId());
            order.setUpdateUserIp(header.getIp());
            orderDao.save(order);
            return ResponseData.success("????????????!",getOrder(order));
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData completedOrderList(String title, int page, int limit,Long start,Long end,HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        if(limit < 1) limit = 1;
        try{
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            Page<Order> orderPage;
            long count = 0;
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"addTime"));
            if (StringUtils.isNotEmpty(title)) {
                orderPage = orderDao.findAllByTitleCompleted(start,end,title,pageable);
            }else {
                orderPage = orderDao.findAllByCompleted(start,end,pageable);
            }
            count = orderDao.sumMoneyBycompleted(start,end,title);
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
            object.put("count",count);
            return ResponseData.success(object);
        }catch (Exception e){
//            e.printStackTrace();
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }
    public ResponseData processedOrderList(String title, int page, int limit,Long start,Long end,HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        if(limit < 1) limit = 1;
        try{
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            Page<Order> orderPage;
            long count = 0;
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"addTime"));
            if (StringUtils.isNotEmpty(title)) {
                orderPage = orderDao.findAllByTitleProcessed(start,end,title,pageable);
            }else {
                orderPage = orderDao.findAllByProcessed(start,end,pageable);
            }
            count = orderDao.sumMoneyByprocessed(start,end,title);
            JSONArray array = new JSONArray();
            for (Order order: orderPage.getContent()) {
                JSONObject object = getOrder(order);
                array.add(object);
            }
            JSONObject object = new JSONObject();
            object.put("total", orderPage.getTotalElements());
            object.put("list",array);
            object.put("count",count);
            return ResponseData.success(object);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData channelChange(String id, String type, String title,
                                      String domain, String mchId, String callbackUrl,
                                      String notifyUrl, String secretKey,
                                      boolean voluntarily, Integer channel, int max,
                                      int mini, int sort, Long limit, String typeCode,
                                      List<Integer> amountList, boolean enabled,
                                      HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            PayList list = payListDao.findById(id);
            if (list == null) throw new Exception("???????????????!");
            list.setTitle(title);
            if (user.isSuperAdmin()){
                list.setType(type);
                list.setChannel(channel);
                list.setTypeCode(typeCode);

                list.setMchId(mchId);
                list.setDomain(domain);
                list.setCallbackUrl(callbackUrl);
                list.setNotifyUrl(notifyUrl);
                list.setSecretKey(secretKey);
            }
            list.setEnabled(enabled);
            list.setVoluntarily(voluntarily);
            list.setAmountList(amountList);
            list.setMax(max);
            list.setMini(mini);
            list.setSort(sort);
            list.setLimit(limit);
            list.setUpdateTime(System.currentTimeMillis());
            payListDao.save(list);
            return ResponseData.success("????????????!",getChannel(list,user.isSuperAdmin()));
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }
    public JSONObject getChannel(PayList list, boolean fully){
        JSONObject object = new JSONObject();
        object.put("id",list.getId());
        object.put("type",list.getType());
        object.put("title",list.getTitle());
        if (fully){
            object.put("domain",list.getDomain());
            object.put("mchId",list.getMchId());
            object.put("callbackUrl",list.getCallbackUrl());
            object.put("notifyUrl",list.getNotifyUrl());
            object.put("secretKey",list.getSecretKey());
            object.put("channel",list.getChannel());
        }
        object.put("typeCode",list.getTypeCode());
        object.put("voluntarily",list.isVoluntarily());
        object.put("amountList",list.getAmountList());
        object.put("enabled",list.isEnabled());
        object.put("max",list.getMax());
        object.put("mini",list.getMini());
        object.put("sort",list.getSort());
        object.put("limit",list.getLimit());
        object.put("addTime",list.getAddTime());
        object.put("updateTime",list.getUpdateTime());
        return object;
    }

    public ResponseData channelEnable(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            PayList list = payListDao.findById(id);
            if (list == null) throw new Exception("???????????????!");
            list.setEnabled(!list.isEnabled());
            list.setUpdateTime(System.currentTimeMillis());
            payListDao.save(list);
            return ResponseData.success(list.isEnabled()?"????????????":"??????"+"??????!",getChannel(list,user.isSuperAdmin()));
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData channelList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            Page<PayList> listPage;
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"sort"));
            if (StringUtils.isNotEmpty(title)){
                listPage = payListDao.findAllByTitle(title,pageable);
            }else {
                listPage = payListDao.findAll(pageable);
            }
            JSONArray array = new JSONArray();
            for (PayList list : listPage.getContent()){
                array.add(getChannel(list,user.isSuperAdmin()));
            }
            JSONObject object = new JSONObject();
            object.put("total", listPage.getTotalElements());
            object.put("list", array);
            return ResponseData.success(object);
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData channelEnableAll(List<String> ids, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            List<PayList> list = payListDao.findAllByIds(ids);
            if (list.isEmpty()) throw new Exception("???????????????!");
            JSONArray array = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setEnabled(!list.get(i).isEnabled());
                array.add(getChannel(list.get(i),user.isSuperAdmin()));
            }
            payListDao.saveAll(list);
            return ResponseData.success("????????????????????????!",array);
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData channelAdd(String type, String title, String domain, String mchId, String callbackUrl, String notifyUrl, String secretKey, boolean voluntarily, Integer channel, Integer max, Integer mini, Integer sort, Long limit, String typeCode, List<Integer> amountList,boolean enabled ,HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isSuperAdmin()) throw new Exception("??????????????????");
            PayList list = new PayList();
            list.setTitle(title);
            list.setType(type);
            list.setChannel(channel);
            list.setTypeCode(typeCode);
            list.setMchId(mchId);
            list.setDomain(domain);
            list.setCallbackUrl(callbackUrl);
            list.setNotifyUrl(notifyUrl);
            list.setSecretKey(secretKey);
            list.setEnabled(enabled);
            list.setVoluntarily(voluntarily);
            list.setAmountList(amountList);
            list.setMax(max);
            list.setMini(mini);
            list.setSort(sort);
            list.setLimit(limit);
            list.setUpdateTime(System.currentTimeMillis());
            list.setAddTime(System.currentTimeMillis());
            if (StringUtils.isEmpty(type)
                    && StringUtils.isEmpty(title)
                    && StringUtils.isEmpty(domain)
                    && StringUtils.isEmpty(mchId)
                    && StringUtils.isEmpty(callbackUrl)
                    && StringUtils.isEmpty(notifyUrl)
                    && StringUtils.isEmpty(secretKey)
                    && StringUtils.isEmpty(typeCode)
            ){
                throw new Exception("????????????????????????");
            }
            payListDao.save(list);
            return ResponseData.success("????????????!",getChannel(list,user.isSuperAdmin()));
        }catch (Exception e){
//            e.printStackTrace();
            return ResponseData.error(""+e.getMessage());
        }
    }

    public ResponseData channelRemoveAll(List<String> ids, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isSuperAdmin()) throw new Exception("????????????");
            List<PayList> list = payListDao.findAllByIds(ids);
            if (list.isEmpty()) throw new Exception("???????????????!");
            payListDao.deleteAll(list);
            return ResponseData.success("??????????????????!");
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData userList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            Page<User> listPage;
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"addTime"));
            if (user.isSuperAdmin()){
                if (StringUtils.isNotEmpty(title)){
                    listPage = userDao.findAllByTitle(title,pageable);
                }else {
                    listPage = userDao.findAll(pageable);
                }
            }else{
                if (StringUtils.isNotEmpty(title)){
                    listPage = userDao.findAdminByTitle(user.getId(),title,pageable);
                }else {
                    listPage = userDao.findAdmin(user.getId(),pageable);
                }
            }
            JSONArray array = new JSONArray();
            for (User u : listPage.getContent()){
                array.add(getUser(u));
            }
            JSONObject object = new JSONObject();
            object.put("total", listPage.getTotalElements());
            object.put("list", array);
            return ResponseData.success(object);
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }
    public JSONObject getUser(User user){
        JSONObject object = new JSONObject();
        object.put("id", user.getId());
        object.put("username", user.getUsername());
        object.put("superiorName", "???????????????");
        if (StringUtils.isNotEmpty(user.getSuperior())){
            User superior = userDao.findById(user.getSuperior());
            if (superior != null){
                object.put("superiorName", superior.getUsername());
            }
        }
//        if (StringUtils.isNotEmpty(user.getRolesId())){
//            RolesList roles = rolesListDao.findById(user.getRolesId());
//            if (roles != null){
//                object.put("rolesName", roles.getName());
//            }
//        }
        if (loginRecordDao.countAllByUserId(user.getId())>0){
            LoginRecord record = loginRecordDao.findByLast(user.getId());
            if (record != null){
                object.put("ip", record.getIp());
                object.put("loginTime", record.getAddTime());
                if(StringUtils.isNotEmpty(record.getHeader())){
                    RequestHeader header = JSONObject.toJavaObject(JSONObject.parseObject(record.getHeader()), RequestHeader.class);
                    object.put("ip", header.getIp());
                    object.put("serverName", header.getServerName());
                }
            }
        }
        object.put("remark", user.getRemark());
        object.put("rolesId", user.getRolesId());
        object.put("enabled", user.isEnabled());
        object.put("admin", user.isAdmin());
        object.put("superAdmin", user.isSuperAdmin());
        object.put("addTime", user.getAddTime());
        object.put("updateTime", user.getUpdateTime());
        return object;
    }

    public ResponseData userEnable(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            User u = userDao.findById(id);
            if (u == null) throw new Exception("???????????????!");
            u.setEnabled(!u.isEnabled());
            u.setUpdateTime(System.currentTimeMillis());
            userDao.save(u);
            if (!u.isEnabled()){
                authDao.removeUser(authDao.findUserByUserId(u.getId()));
            }
            return ResponseData.success(u.isEnabled()?"????????????":"??????"+"??????!",getUser(u));
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData userChange(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            User u = userDao.findById(id);
            if (u == null) throw new Exception("???????????????!");
//            if (u.getId().equals(user.getId())) throw new Exception("????????????????????????????????????!");
            u.setSalt(ToolsUtil.getSalt());
            String password = ToolsUtil.getRandom(12);
            MD5Util md5 = new MD5Util(u.getSalt());
            u.setPassword(md5.getPassWord(md5.getMD5(password)));
            u.setUpdateTime(System.currentTimeMillis());
            userDao.save(u);
            loginRecordDao.save(new LoginRecord(u.getId(),"??????????????????",""));
            authDao.removeUser(authDao.findUserByUserId(u.getId()));
            return ResponseData.success("??????????????????!",ResponseData.object("password",password));
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData userAdd(String id,String username, boolean admin, boolean superAdmin, boolean enabled, String rolesId,String remark, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            username = username.replaceAll(" ","").trim();
            if (StringUtils.isEmpty(username)
//                    || (!admin && !superAdmin && StringUtils.isEmpty(rolesId))
            ) throw new Exception("??????????????????");
            User profile;
            if (StringUtils.isNotEmpty(id)){
                profile = userDao.findById(id);
                if (profile == null) throw new Exception("???????????????!");
            }else{
                profile = new User();
            }
            User u = userDao.findByUsername(username);
            if (u != null && !u.getId().equals(profile.getId())) throw new Exception("??????????????????!");
            profile.setUsername(username);
            profile.setAdmin(admin);
            if(user.isSuperAdmin()){
                profile.setSuperAdmin(superAdmin);
            }
            profile.setEnabled(enabled);
            profile.setSuperior(user.getId());
            profile.setRemark(remark);
            RolesList roles = rolesListDao.findById(rolesId);
            if (roles != null){
                profile.setRolesId(rolesId);
            }
            userDao.save(profile);
            JSONObject object = getUser(profile);
            if(StringUtils.isEmpty(id)){
                profile.setSalt(ToolsUtil.getSalt());
                String password = ToolsUtil.getRandom(8);
                MD5Util md5 = new MD5Util(profile.getSalt());
                profile.setPassword(md5.getPassWord(md5.getMD5(password)));
                userDao.save(profile);
                object.put("password",password);
            }
            return ResponseData.success("??????"+(id != null?"??????":"??????")+"??????!",object);
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData routerList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        try {
            return ResponseData.success();
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData userRemoveAll(List<String> ids, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isSuperAdmin()) throw new Exception("????????????");
            List<User> list = userDao.findAllByIds(ids);
            if (list.isEmpty()) throw new Exception("???????????????!");
            userDao.deleteAll(list);
            shortLinkDao.deleteByUserIds(ids);
            shortLinkRecordDao.deleteByUserIds(ids);
            loginRecordDao.deleteByUserIds(ids);
            loginRecordFailDao.deleteByUserIds(ids);
            return ResponseData.success("??????????????????!");
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData clientList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            Page<Client> clientPage;
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"addTime"));
            if (user.isSuperAdmin() || user.isAdmin()){
                if (StringUtils.isNotEmpty(title)){
                    clientPage = clientDao.findAllByToken(title,pageable);
                }else {
                    clientPage = clientDao.findAll(pageable);
                }
            }else{
                if (StringUtils.isNotEmpty(title)){
                    clientPage = clientDao.findUserByUserId(user.getId(),title,pageable);
                }else {
                    clientPage = clientDao.findAllByUserId(user.getId(),pageable);
                }
            }
            JSONArray array = new JSONArray();
            for (Client client : clientPage.getContent()){
                array.add(getClient(client,user.isSuperAdmin() || user.isAdmin()));
            }
            JSONObject object = new JSONObject();
            object.put("total", clientPage.getTotalElements());
            object.put("list", array);
            return ResponseData.success(object);
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }
    public JSONObject getClient(Client client, boolean admin){
        JSONObject object = new JSONObject();
        object.put("id", client.getId());
        if (StringUtils.isNotEmpty(client.getRegisterHeader())){
            RequestHeader header = JSONObject.toJavaObject(JSONObject.parseObject(client.getRegisterHeader()), RequestHeader.class);
            object.put("ip", header.getIp());
            if (admin){
                object.put("serverName", header.getServerName());
                User user = userDao.findById(client.getUserId());
                if (user != null){
                    object.put("username", user.getUsername());
                }
                object.put("url", header.getUrl());
            }
        }
        if (StringUtils.isNotEmpty(client.getUpdateHeader())){
            RequestHeader header = JSONObject.toJavaObject(JSONObject.parseObject(client.getUpdateHeader()), RequestHeader.class);
            object.put("ipUpdate", header.getIp());
            if (admin){
                object.put("serverNameUpdate", header.getServerName());
                object.put("urlUpdate", header.getUrl());
            }
        }
        object.put("addTime", client.getAddTime());
        object.put("updateTime", client.getUpdateTime());
        return object;
    }

    public ResponseData clientRemove(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
//            if (!user.isSuperAdmin()) throw new Exception("????????????");
            Client client = clientDao.findById(id);
            if (client == null) throw new Exception("???????????????!");
            if (user.isSuperAdmin()){
                clientDao.delete(client);
            }else{
                client.setUserId(null);
                clientDao.save(client);
            }
            return ResponseData.success("????????????!");
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData clientRemoveAll(List<String> ids, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
//            if (!user.isSuperAdmin()) throw new Exception("????????????");
            List<Client> list = clientDao.findAllByIds(ids);
            if (list.isEmpty()) throw new Exception("???????????????!");
            if (user.isSuperAdmin()){
                clientDao.deleteAll(list);
            }else{
                for (Client client : list) {
                    client.setUserId(null);
                }
                clientDao.saveAll(list);
            }
            return ResponseData.success("??????????????????!");
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData clientAdd(HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (shortLinkDao.countAllByInactivated(user.getId()) > SHORT_LINK_MAX){
                throw new Exception("????????????????????????????????? "+SHORT_LINK_MAX+"???");
            }
            ShortLink link = new ShortLink(user.getId(),null);
            while (shortLinkDao.countAllByShortLink(link.getShortLink()) > 0){
                link.setShortLink(ToolsUtil.getRandom(7));
            }
            shortLinkDao.save(link);
            return ResponseData.success("????????????????????????!",ResponseData.object("url",shortLinkService.greaterUrl(link)));
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData basicOrderClean(HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            orderDao.deleteAllByTradeStatus(false, TimeUtil.getTodayZero());
            return ResponseData.success("??????????????????!");
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData shortLinkList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            Page<ShortLink> linkPage;
            Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"addTime"));
            if (user.isSuperAdmin() || user.isAdmin()){
                if (StringUtils.isNotEmpty(title)){
                    linkPage = shortLinkDao.findAllByTitle(title,pageable);
                }else {
                    linkPage = shortLinkDao.findAll(pageable);
                }
            }else{
                if (StringUtils.isNotEmpty(title)){
                    linkPage = shortLinkDao.findTitleByUserId(user.getId(),title,pageable);
                }else {
                    linkPage = shortLinkDao.findAllByUserId(user.getId(),pageable);
                }
            }
            JSONArray array = new JSONArray();
            for (ShortLink link : linkPage.getContent()){
                array.add(getShortLink(link,user.isSuperAdmin() || user.isAdmin()));
            }
            JSONObject object = new JSONObject();
            object.put("total", linkPage.getTotalElements());
            object.put("list", array);
            return ResponseData.success(object);
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }
    public JSONObject getShortLink(ShortLink shortLink, boolean admin){
        JSONObject object = new JSONObject();
        object.put("id", shortLink.getId());
        object.put("clientId", shortLink.getClientId());
        object.put("shortLink", shortLink.getShortLink());
        object.put("url", shortLink.getUrl());
        object.put("jump", shortLinkService.greaterUrl(shortLink));
        if (StringUtils.isNotEmpty(shortLink.getUserId()) && admin){
            User user = userDao.findById(shortLink.getUserId());
            if (user != null){
                object.put("username", user.getUsername());
            }
        }
        object.put("count", shortLinkRecordDao.countAllById(shortLink.getId()));
        ShortLinkRecord record = shortLinkRecordDao.findByFirst(shortLink.getId());
        if (record != null){
            RequestHeader header = JSONObject.toJavaObject(JSONObject.parseObject(record.getHeader()), RequestHeader.class);
            object.put("ip", header.getIp());
            if (admin){
                object.put("serverName", header.getServerName());
            }
        }
        record = shortLinkRecordDao.findByLast(shortLink.getId());
        if (record != null){
            RequestHeader header = JSONObject.toJavaObject(JSONObject.parseObject(record.getHeader()), RequestHeader.class);
            object.put("ipUpdate", header.getIp());
            if (admin){
                object.put("serverNameUpdate", header.getServerName());
            }
        }
        object.put("addTime", shortLink.getAddTime());
        return object;
    }

    public ResponseData shortLinkRemove(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            ShortLink shortLink = shortLinkDao.findById(id);
            if (shortLink == null) throw new Exception("???????????????!");
            if (user.isSuperAdmin()){
                if (shortLinkRecordDao.countAllById(shortLink.getId()) == 0){
                    shortLinkDao.delete(shortLink);
                }else{
                    throw new Exception("?????????????????????????????????");
                }
            }else{
                if (shortLinkRecordDao.countAllById(shortLink.getId()) == 0){
                    shortLink.setUserId(null);
                    shortLinkDao.save(shortLink);
                }else{
                    throw new Exception("?????????????????????????????????");
                }
            }
            return ResponseData.success("????????????!");
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData shortLinkRemoveAll(List<String> ids, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            List<ShortLink> list = shortLinkDao.findAllById(ids);
            if (list.size() == 0) throw new Exception("???????????????!");
            for (ShortLink link: list) {
                if (user.isSuperAdmin()){
                    if (shortLinkRecordDao.countAllById(link.getId()) == 0){
                        shortLinkDao.delete(link);
                    }
                }else{
                    if (shortLinkRecordDao.countAllById(link.getId()) == 0){
                        link.setUserId(null);
                        shortLinkDao.save(link);
                    }
                }
            }
            return ResponseData.success("??????????????????!???????????????????????????????????????????????????????????????????????????");
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData userAdmin(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????");
            User u = userDao.findById(id);
            if (u == null) throw new Exception("???????????????!");
            if (u.isSuperAdmin() && !user.isSuperAdmin()) throw new Exception("??????????????????????????????????????????!");
            u.setAdmin(!u.isAdmin());
            if(u.isSuperAdmin()){
               u.setSuperAdmin(false);
            }
            u.setUpdateTime(System.currentTimeMillis());
            userDao.save(u);
            authDao.removeUser(authDao.findUserByUserId(u.getId()));
            return ResponseData.success("???????????????????????????!",getUser(u));
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData userSuper(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            if (!user.isSuperAdmin()) throw new Exception("??????????????????????????????");
            User u = userDao.findById(id);
            if (u == null) throw new Exception("???????????????!");
            u.setSuperAdmin(!u.isSuperAdmin());
            if(u.isAdmin()){
                u.setAdmin(false);
            }
            u.setUpdateTime(System.currentTimeMillis());
            userDao.save(u);
            authDao.removeUser(authDao.findUserByUserId(u.getId()));
            return ResponseData.success("?????????????????????????????????!",getUser(u));
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    public ResponseData userRemark(String remark, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"???????????????");
            User user = header.getUser();
            user.setRemark(remark);
            userDao.save(user);
            authDao.removeUser(authDao.findUserByUserId(user.getId()));
            return ResponseData.success("??????????????????!",getUserInfo(user));
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }

    private JSONObject getUserInfo(User user) {
        JSONObject object = new JSONObject();
        object.put("username", user.getUsername());
        object.put("superAdmin",user.isSuperAdmin());
        object.put("admin", user.isAdmin());
        object.put("avatar", user.getAvatar());
        object.put("token",user.getToken());
//        object.put("remark",user.getRemark());
        object.put("permissions", RoleUtil.getPermissions(user));
        return object;
    }

    public ResponseData userLogout(HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error("???????????????");
            User user = header.getUser();
            authDao.removeUser(authDao.findUserByUserId(user.getId()));
            return ResponseData.success("??????????????????!");
        }catch (Exception e){
            return ResponseData.error("???????????????"+e.getMessage());
        }
    }
}
