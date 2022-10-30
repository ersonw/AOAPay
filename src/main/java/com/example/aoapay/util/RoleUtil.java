package com.example.aoapay.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.table.RouterList;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RoleUtil {
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
//    实现 增删查改 权限判断 并且下发前端
}
