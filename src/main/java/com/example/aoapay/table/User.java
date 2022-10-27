package com.example.aoapay.table;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.util.ToolsUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.GeneratedValue;
import java.util.ArrayList;
import java.util.List;

@ToString(includeFieldNames = true)
@Setter
@Getter
@Document(collection = "user")
public class User {
    @Id
    @GeneratedValue
    private String id;
    private String superior=null;
    private String inviteCode=null;
    private String username=null;
    private String avatar=null;
    private String password=null;
    private String salt=null;
    private String rolesId = null;
    private boolean admin =false;
    private boolean superAdmin =false;
    private Long  addTime=0L;
    private Long updateTime = 0L;
    @Transient
    private String token;
    public User() {
//        id = ToolsUtil.getToken();
    }
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public static User getUser(String user) {
        if (user != null){
            return JSONObject.toJavaObject(JSONObject.parseObject(user),User.class);
        }
        return null;
    }
}
