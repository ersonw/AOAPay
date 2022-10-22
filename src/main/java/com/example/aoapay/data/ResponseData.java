package com.example.aoapay.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(includeFieldNames = true)
public class ResponseData {
    private  int code = 200;
    private  String message;
    private  Object data;

    public static ResponseData success(String message, Object object) {
        return new ResponseData(message,object);
    }

    public ResponseData() {}
    public ResponseData(String message) {
        this.message = message;
    }
    public ResponseData(String message, Object data) {
        this.message = message;
        this.data = data;
    }
    public ResponseData(JSONObject data) {
        this.data = data;
    }
    public ResponseData(int code) {
        this.code = code;
    }
    public ResponseData(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public ResponseData(int code, Object data) {
        this.code = code;
        this.data = data;
    }
    public ResponseData(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static ResponseData success() {
        return new ResponseData("");
    }
    public static ResponseData success(Object data) {
        return new ResponseData(200, data);
    }
    public static ResponseData success(JSONArray array) {
        return new ResponseData(object("list", array));
    }

    public static ResponseData fail() {
        return new ResponseData(404);
    }
    public static ResponseData error() {
        return new ResponseData(404);
    }
    public static ResponseData error(int code) {
        return new ResponseData(code);
    }
    public static ResponseData error(int code, String message) {
        return new ResponseData(code, message);
    }
    public static ResponseData fail(String message) {
        return new ResponseData(404,message);
    }

    public static ResponseData error(String message) {
        return fail(message);
    }
    public static JSONObject object(String name, Object val) {
        JSONObject obj = new JSONObject();
        obj.put(name,val);
        return obj;
    }

    public static ResponseData success(String message) {
        return new ResponseData(200,message);
    }
}
