package com.example.aoapay.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.table.RouterList;
import com.example.aoapay.table.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RoleUtil {
    public static JSONArray getSuperAdminRouterList(){
        JSONArray array = new JSONArray();
//        array.add(getDashboard());
        array.add(getClient());
        array.add(getShortLink(0));
        array.add(getOrder(0));
        array.add(getChannel());
        array.add(getUser());
        array.add(getRole(0));
        return array;
    }
    public static JSONArray getAdminRouterList(){
        JSONArray array = new JSONArray();
//        array.add(getDashboard());
        array.add(getClient());
        array.add(getShortLink(1));
        array.add(getOrder(0));
        array.add(getChannel());
        array.add(getUser());
        return array;
    }
    public static JSONArray getUserRouterList(){
        JSONArray array = new JSONArray();
//        array.add(getDashboard());
        array.add(getClient());
        array.add(getShortLink(1));
        array.add(getOrder(3));
        return array;
    }
    public static JSONObject getShortLink(int level){
        JSONObject object = new JSONObject();
        if (level==0){
            object.put("title","短链接管理");
            object.put("key","/app/short");
            JSONArray subs = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("key","/app/short/list");
            json.put("title","所有链接");
            json.put("component","ShortLink");
            subs.add(json);
            json = new JSONObject();
            json.put("key","/app/short/record");
            json.put("title","访问记录");
            json.put("component","ShortLink");
            subs.add(json);
            object.put("subs",subs);
        }else if (level==1){
            object.put("key","/app/short/list");
            object.put("title","短链接管理");
            object.put("component","ShortLink");
        }else if (level==2){
            object.put("key","/app/short/record");
            object.put("title","访问记录");
            object.put("component","ShortLink");
        }
        return object;
    }
    public static JSONObject getDashboard(){
        JSONObject object = new JSONObject();
        object.put("title","首页");
        object.put("key","/app/dashboard/index");
        object.put("component","Dashboard");
        return object;
    }
    public static JSONObject getClient(){
        JSONObject object = new JSONObject();
        object.put("title","客户端管理");
        object.put("key","/app/client/index");
        object.put("component","Client");
        return object;
    }
    public static JSONObject getChannel(){
        JSONObject object = new JSONObject();
        object.put("title","渠道管理");
        object.put("key","/app/channel/list");
        object.put("component","Channel");
        return object;
    }
    public static JSONObject getUser(){
        JSONObject object = new JSONObject();
        object.put("title","用户管理");
        object.put("key","/app/user/list");
        object.put("component","UserList");
        return object;
    }
    public static JSONObject getOrder(int level){
        JSONObject object = new JSONObject();
        if (level==0){
            object.put("title","订单管理");
            object.put("key","/app/order");
            JSONArray subs = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("key","/app/order/processedOrder");
            json.put("title","待处理订单");
            json.put("component","ProcessedOrder");
            subs.add(json);
            json = new JSONObject();
            json.put("key","/app/order/completedOrder");
            json.put("title","已完成订单");
            json.put("component","CompletedOrder");
            subs.add(json);
            json = new JSONObject();
            json.put("key","/app/order/basicOrder");
            json.put("title","所有订单");
            json.put("component","BasicOrder");
            subs.add(json);
            object.put("subs",subs);
        }else if (level==1){
            object.put("key","/app/order/processedOrder");
            object.put("title","待处理订单");
            object.put("component","ProcessedOrder");
        }else if (level==2){
            object.put("key","/app/order/completedOrder");
            object.put("title","已完成订单");
            object.put("component","CompletedOrder");
        }else if(level==3){
            object.put("key","/app/order/basicOrder");
            object.put("title","所有订单");
            object.put("component","BasicOrder");
        }
        return object;
    }
    public static JSONObject getRole(int level){
        JSONObject object = new JSONObject();
        if (level==0){
            object.put("title","权限管理");
            object.put("key","/app/role");
            JSONArray subs = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("key","/app/role/roles/list");
            json.put("title","角色管理");
            json.put("component","RolesList");
            subs.add(json);
            json = new JSONObject();
            json.put("key","/app/role/router/list");
            json.put("title","路由管理");
            json.put("component","RouterList");
            subs.add(json);
            object.put("subs",subs);
        }else if (level==1){
            object.put("key","/app/role/roles/list");
            object.put("title","角色管理");
            object.put("component","RolesList");
        }else if (level==2){
            object.put("key","/app/role/router/list");
            object.put("title","路由管理");
            object.put("component","RouterList");
        }
        return object;
    }
    public static JSONArray getRouters(List<RouterList> list) {
        JSONArray array = new JSONArray();
        List<RouterList> routers = new ArrayList<>();
        List<RouterList> subs = new ArrayList<>();
        for (RouterList router: list) {
            //
        }
        return array;
    }
    public static JSONObject getRouter(RouterList router){
        JSONObject object = new JSONObject();
        object.put("key", router.getKey());
        object.put("title", router.getTitle());
        object.put("icon", router.getIcon());
        object.put("component", router.getComponent());
        return object;
    }

    public static JSONArray getPermissions(User user) {
        JSONArray array = new JSONArray();
        if (user.isSuperAdmin()){
            array = getSuperAdminRouterList();
        }else if (user.isAdmin()){
            array = getAdminRouterList();
        }else{
            array = getUserRouterList();
        }
        JSONArray roles = new JSONArray();
        for (Object object: array) {
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(object));
            roles.add(json.getString("key"));
            if (json.get("subs") instanceof JSONArray){
                JSONArray a = json.getJSONArray("subs");
                for (int i = 0; i < a.size(); i++) {
                    roles.add(json.getString("key"));
                }
            }
        }
        return roles;
    }
//    实现 增删查改 权限判断 并且下发前端
}
