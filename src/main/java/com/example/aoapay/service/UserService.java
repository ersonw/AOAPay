package com.example.aoapay.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.dao.*;
import com.example.aoapay.data.RequestHeader;
import com.example.aoapay.table.LoginRecord;
import com.example.aoapay.table.LoginRecordFail;
import com.example.aoapay.table.RouterList;
import com.example.aoapay.table.User;
import com.example.aoapay.util.MD5Util;
import com.example.aoapay.util.RoleUtil;
import com.example.aoapay.util.TimeUtil;
import com.example.aoapay.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private LoginRecordDao loginRecordDao;
    @Autowired
    private LoginRecordFailDao loginRecordFailDao;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private RouterListDao routerListDao;
    @Autowired
    private RoleListDao roleListDao;
    @Autowired
    private RolesListDao rolesListDao;

    private static final int COUNT_PASSWORD_FAIL = 5;
    public User auth(HttpServletRequest request) throws Exception {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        if (header.getUser() == null) throw new Exception("用户不存在!");
        return header.getUser();
    }
    public long getUserLoginFail(String userId) throws Exception {
        long today = TimeUtil.getTodayZero();
        long lastTime = loginRecordDao.getLastLoginTime(userId);
        long time = Math.max(lastTime, today);
        long count = loginRecordFailDao.countAllByUserId(userId, time);
        if (COUNT_PASSWORD_FAIL <= count) throw new Exception("今日已超过密码重试次数，请明日再试!");
        return COUNT_PASSWORD_FAIL - count;
    }
    public User authUser(String username, String password, RequestHeader header) throws Exception {
        User user = userDao.findByUsername(username);
        if (user == null) throw new Exception("用户不存在！");
        long count = getUserLoginFail(user.getId());
        MD5Util md5 = new MD5Util(user.getSalt());
        if (!md5.getPassWord(password).equals(user.getPassword())) {
            loginRecordFailDao.save(new LoginRecordFail(user.getId(), header.getIp(), JSONObject.toJSONString(header)));
            throw new Exception("用户密码错误！剩余"+(count-1)+"次");
        }
        loginRecordDao.save(new LoginRecord(user.getId(), header.getIp(), JSONObject.toJSONString(header)));
        user.setToken(ToolsUtil.getToken());
        authDao.pushUser(user);
        return user;
    }
    public JSONArray getRouters(User user){
        List<RouterList> list = new ArrayList<>();
        if (user.isSuperAdmin()){
            list = routerListDao.findAllBySub();
        }else if (user.isAdmin()){
        }else{
        }
        return RoleUtil.getRouters(list);
    }
}
