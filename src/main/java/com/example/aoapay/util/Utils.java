package com.example.aoapay.util;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.dao.AuthDao;
import com.example.aoapay.dao.ClientDao;
import com.example.aoapay.dao.UserDao;
import com.example.aoapay.data.RequestHeader;
import com.example.aoapay.table.Client;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Getter
@Component
public class Utils {
    private static Utils self;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private UserDao userDao;
    @PostConstruct
    public void initPost(){
        self = this;
        System.out.printf("加载器成功！\n");
    }
    public static AuthDao getAuthDao() {
        return self.authDao;
    }
    public static ClientDao getClientDao() {
        return self.clientDao;
    }
    public static UserDao getUserDao() {
        return self.userDao;
    }
    public static void checkClient(HttpServletRequest request, HttpServletResponse response){
        RequestHeader headers = ToolsUtil.getRequestHeaders(request);
        Client client = headers.getClient();
        if (client == null){
            client = new Client(JSONObject.toJSONString(headers));
            response.addCookie(new Cookie("clientId", client.getId()));
        }
        client.setUpdateTime(System.currentTimeMillis());
        client.setUpdateHeader(JSONObject.toJSONString(headers));
        self.clientDao.save(client);
    }
    public static Client addClient(HttpServletRequest request, HttpServletResponse response){
        RequestHeader headers = ToolsUtil.getRequestHeaders(request);
        Client client = new Client(JSONObject.toJSONString(headers));
        response.addCookie(new Cookie("clientId", client.getId()));
        self.clientDao.save(client);
        return client;
    }
}
