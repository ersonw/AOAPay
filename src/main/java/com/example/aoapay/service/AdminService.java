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


    public ResponseData menu(HttpServletRequest request) {

        try{
            JSONArray array = new JSONArray();
            User user = userService.auth(request);
            array = userService.getRouters(user);
            return ResponseData.success(array);
        } catch (Exception e) {
            log.error("Error menu {}", e.getMessage());
            return ResponseData.error(201,"错误："+e.getMessage());
        }
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
            object.put("permissions", RoleUtil.getPermissions(user));
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
        object.put("link",shortLinkService.greaterLink(order.getClientId(),order.getUserId()));
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
//            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
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
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData basicOrderConfirm(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
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
            return ResponseData.error("错误提示："+e.getMessage());
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
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
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
            return ResponseData.error("错误提示："+e.getMessage());
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
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
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
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData channelChange(String id, String type, String title, String domain, String mchId, String callbackUrl, String notifyUrl, String secretKey, boolean voluntarily, Integer channel, int max, int mini, int sort, Long limit, String typeCode, List<Integer> amountList,HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
            PayList list = payListDao.findById(id);
            if (list == null) throw new Exception("渠道不存在!");
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
            list.setVoluntarily(voluntarily);
            list.setAmountList(amountList);
            list.setMax(max);
            list.setMini(mini);
            list.setSort(sort);
            list.setLimit(limit);
            list.setUpdateTime(System.currentTimeMillis());
            payListDao.save(list);
            return ResponseData.success("修改成功!",getChannel(list,user.isSuperAdmin()));
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
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
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
            PayList list = payListDao.findById(id);
            if (list == null) throw new Exception("渠道不存在!");
            list.setEnabled(!list.isEnabled());
            list.setUpdateTime(System.currentTimeMillis());
            payListDao.save(list);
            return ResponseData.success(list.isEnabled()?"解除禁用":"禁用"+"成功!",getChannel(list,user.isSuperAdmin()));
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData channelList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
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
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData channelEnableAll(List<String> ids, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
            List<PayList> list = payListDao.findAllByIds(ids);
            if (list.isEmpty()) throw new Exception("渠道不存在!");
            JSONArray array = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setEnabled(!list.get(i).isEnabled());
                array.add(getChannel(list.get(i),user.isSuperAdmin()));
            }
            payListDao.saveAll(list);
            return ResponseData.success("全部切换状态成功!",array);
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData channelAdd(String type, String title, String domain, String mchId, String callbackUrl, String notifyUrl, String secretKey, boolean voluntarily, Integer channel, Integer max, Integer mini, Integer sort, Long limit, String typeCode, List<Integer> amountList, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isSuperAdmin()) throw new Exception("没有添加权限");
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
                throw new Exception("参数不允许为空！");
            }
            payListDao.save(list);
            return ResponseData.success("添加成功!",getChannel(list,user.isSuperAdmin()));
        }catch (Exception e){
//            e.printStackTrace();
            return ResponseData.error(""+e.getMessage());
        }
    }

    public ResponseData channelRemoveAll(List<String> ids, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isSuperAdmin()) throw new Exception("没有权限");
            List<PayList> list = payListDao.findAllByIds(ids);
            if (list.isEmpty()) throw new Exception("渠道不存在!");
            payListDao.deleteAll(list);
            return ResponseData.success("全部删除成功!");
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData userList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
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
                    listPage = userDao.findAdminByTitle(title,pageable);
                }else {
                    listPage = userDao.findAdmin(pageable);
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
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }
    public JSONObject getUser(User user){
        JSONObject object = new JSONObject();
        object.put("id", user.getId());
        object.put("username", user.getUsername());
        object.put("superiorName", "系统管理员");
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
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
            User u = userDao.findById(id);
            if (u == null) throw new Exception("用户不存在!");
            u.setEnabled(!u.isEnabled());
            u.setUpdateTime(System.currentTimeMillis());
            userDao.save(u);
            if (!u.isEnabled()){
                authDao.removeUser(authDao.findUserByUserId(u.getId()));
            }
            return ResponseData.success(u.isEnabled()?"解除禁用":"禁用"+"成功!",getUser(u));
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData userChange(String id, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
            User u = userDao.findById(id);
            if (u == null) throw new Exception("用户不存在!");
            if (u.getId().equals(user.getId())) throw new Exception("不可通过列表重置自身密码!");
            u.setSalt(ToolsUtil.getSalt());
            String password = ToolsUtil.getRandom(8);
            MD5Util md5 = new MD5Util(u.getSalt());
            u.setPassword(md5.getPassWord(md5.getMD5(password)));
            u.setUpdateTime(System.currentTimeMillis());
            userDao.save(u);
            authDao.removeUser(authDao.findUserByUserId(u.getId()));
            return ResponseData.success("密码重置成功!",ResponseData.object("password",password));
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData userAdd(String username, boolean admin, boolean superAdmin, boolean enabled, String rolesId,String remark, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
            username = username.replaceAll(" ","").trim();
            if (StringUtils.isEmpty(username)
//                    || (!admin && !superAdmin && StringUtils.isEmpty(rolesId))
            ) throw new Exception("参数不完整！");
            User profile = userDao.findByUsername(username);
            if (profile != null) throw new Exception("用户名已存在!");
            profile = new User();
            profile.setUsername(username);
            if(user.isSuperAdmin()){
                profile.setAdmin(admin);
                profile.setSuperAdmin(admin);
            }
            profile.setEnabled(enabled);
            profile.setSuperior(user.getId());
            profile.setRemark(remark);
//            RolesList roles = rolesListDao.findById(rolesId);
//            if (roles != null){
//                profile.setRolesId(rolesId);
//            }
            profile.setSalt(ToolsUtil.getSalt());
            String password = ToolsUtil.getRandom(8);
            MD5Util md5 = new MD5Util(profile.getSalt());
            profile.setPassword(md5.getPassWord(md5.getMD5(password)));
            userDao.save(profile);
            JSONObject object = getUser(profile);
            object.put("password",password);
            return ResponseData.success("用户添加成功!",object);
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData routerList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isSuperAdmin()) throw new Exception("没有管理权限!");
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
                    listPage = userDao.findAdminByTitle(title,pageable);
                }else {
                    listPage = userDao.findAdmin(pageable);
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
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData userRemoveAll(List<String> ids, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isSuperAdmin()) throw new Exception("没有权限");
            List<User> list = userDao.findAllByIds(ids);
            if (list.isEmpty()) throw new Exception("用户不存在!");
            userDao.deleteAll(list);
            return ResponseData.success("全部删除成功!");
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData clientList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
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
            return ResponseData.error("错误提示："+e.getMessage());
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
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
//            if (!user.isSuperAdmin()) throw new Exception("没有权限");
            Client client = clientDao.findById(id);
            if (client == null) throw new Exception("数据不存在!");
            if (user.isSuperAdmin()){
                clientDao.delete(client);
            }else{
                client.setUserId(null);
                clientDao.save(client);
            }
            return ResponseData.success("删除成功!");
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData clientRemoveAll(List<String> ids, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
//            if (!user.isSuperAdmin()) throw new Exception("没有权限");
            List<Client> list = clientDao.findAllByIds(ids);
            if (list.isEmpty()) throw new Exception("数据不存在!");
            if (user.isSuperAdmin()){
                clientDao.deleteAll(list);
            }else{
                for (Client client : list) {
                    client.setUserId(null);
                }
                clientDao.saveAll(list);
            }
            return ResponseData.success("全部删除成功!");
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData clientAdd(HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (shortLinkDao.countAllByInactivated(user.getId()) > SHORT_LINK_MAX){
                throw new Exception("已超出最大未激活生成数 "+SHORT_LINK_MAX+"条");
            }
            ShortLink link = new ShortLink(user.getId(),null);
            while (shortLinkDao.countAllByShortLink(link.getShortLink()) > 0){
                link.setShortLink(ToolsUtil.getRandom(7));
            }
            shortLinkDao.save(link);
            return ResponseData.success("生成邀请链接成功!",ResponseData.object("url",shortLinkService.greaterUrl(link)));
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData basicOrderClean(HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            if (!user.isAdmin() && !user.isSuperAdmin()) throw new Exception("非管理员用户");
            orderDao.deleteAllByTradeStatus(false, TimeUtil.getTodayZero());
            return ResponseData.success("全部清理成功!");
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData shortLinkList(String title, int page, int limit, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        page--;
        if (page < 0) page = 0;
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
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
            return ResponseData.error("错误提示："+e.getMessage());
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
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            ShortLink shortLink = shortLinkDao.findById(id);
            if (shortLink == null) throw new Exception("记录不存在!");
            if (user.isSuperAdmin()){
                if (shortLinkRecordDao.countAllById(shortLink.getId()) == 0){
                    shortLinkDao.delete(shortLink);
                }else{
                    throw new Exception("无法删除已激活的链接！");
                }
            }else{
                if (shortLinkRecordDao.countAllById(shortLink.getId()) == 0){
                    shortLink.setUserId(null);
                    shortLinkDao.save(shortLink);
                }else{
                    throw new Exception("无法删除已激活的链接！");
                }
            }
            return ResponseData.success("删除成功!");
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }

    public ResponseData shortLinkRemoveAll(List<String> ids, HttpServletRequest request) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        try {
            if (header.getUser() == null) return ResponseData.error(201,"未登录用户");
            User user = header.getUser();
            List<ShortLink> list = shortLinkDao.findAllById(ids);
            if (list.size() == 0) throw new Exception("记录不存在!");
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
            return ResponseData.success("全部删除成功!部分已激活链接无法删除，如需强制删除，请联系管理员");
        }catch (Exception e){
            return ResponseData.error("错误提示："+e.getMessage());
        }
    }
}
