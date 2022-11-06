package com.example.aoapay.data;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.table.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel
public  class pData  {
    public pData(){}

    @ApiModelProperty(name = "payListId", value= "支付方式ID",required = false)
    private String payListId;
    @ApiModelProperty(name = "name", value= "姓名",required = false)
    private String name;
    @ApiModelProperty(name = "amount", value= "金额",required = false)
    private Double amount;
    @ApiModelProperty(name = "username", value= "用户账号或者手机号",required = false)
    private String username;
    @ApiModelProperty(name = "password", value= "用户密码",required = false)
    private String password;
    @ApiModelProperty(name = "deviceId", value= "设备ID",required = false)
    private String deviceId;
    @ApiModelProperty(name = "platform", value= "设备名称",required = false)
    private String platform;

    @ApiModelProperty(name = "phone", value= "手机号",required = false)
    private String phone;
    @ApiModelProperty(name = "codeId", value= "验证码ID",required = false)
    private String codeId;
    @ApiModelProperty(name = "code", value= "验证码",required = false)
    private String code;

    @ApiModelProperty(name = "seek", value= "视频刻度",required = false)
    private long seek;

    @ApiModelProperty(name = "id", value= "唯一ID",required = false)
    private String id;
    @ApiModelProperty(name = "type", value= "类型",required = false)
    private String type;
    @ApiModelProperty(name = "title", value= "标题",required = false)
    private String title;
    @ApiModelProperty(name = "domain", value= "域名",required = false)
    private String domain;
    @ApiModelProperty(name = "mchId", value= "商户ID",required = false)
    private String mchId;
    @ApiModelProperty(name = "callbackUrl", value= "同步返回地址",required = false)
    private String callbackUrl;
    @ApiModelProperty(name = "notifyUrl", value= "异步返回地址",required = false)
    private String notifyUrl;
    @ApiModelProperty(name = "secretKey", value= "商户密钥",required = false)
    private String secretKey;
    @ApiModelProperty(name = "voluntarily", value= "可编辑",required = false)
    private boolean voluntarily;
    @ApiModelProperty(name = "enabled", value= "开启",required = false)
    private boolean enabled;
    @ApiModelProperty(name = "channel", value= "渠道",required = false)
    private int channel;
    @ApiModelProperty(name = "max", value= "最大",required = false)
    private int max;
    @ApiModelProperty(name = "mini", value= "最小",required = false)
    private int mini;
    @ApiModelProperty(name = "sort", value= "排序",required = false)
    private int sort;
    @ApiModelProperty(name = "limit", value= "限制",required = false)
    private long limit;
    @ApiModelProperty(name = "typeCode", value= "类型代码",required = false)
    private String typeCode;
    @ApiModelProperty(name = "amountList", value= "固定金额",required = false)
    private List<Integer> amountList = new ArrayList<>();

    @ApiModelProperty(name = "ids", value= "唯一ID",required = false)
    private List<String> ids;
    @ApiModelProperty(name = "toId", value= "目标ID",required = false)
    private long toId;

    @ApiModelProperty(name = "duration", value= "目标ID",required = false)
    private long duration;

    @ApiModelProperty(name = "text", value= "字符串",required = false)
    private String text;
    @ApiModelProperty(name = "oldValue", value= "旧数据",required = false)
    private String oldValue;
    @ApiModelProperty(name = "newValue", value= "新数据",required = false)
    private String newValue;


    @ApiModelProperty(name = "files", value= "文件列表",required = false)
    private String files;
    @ApiModelProperty(name = "filePath", value= "文件路径",required = false)
    private String filePath;
    @ApiModelProperty(name = "imagePath", value= "图片路径",required = false)
    private String imagePath;

    @ApiModelProperty(name = "selected", value= "批量选择",required = false)
    private List<String> selected;

    @ApiModelProperty(hidden = true)
    private String ip;
    @ApiModelProperty(hidden = true)
    private String serverName;
    @ApiModelProperty(hidden = true)
    private int serverPort;
    @ApiModelProperty(hidden = true)
    private String uri;
    @ApiModelProperty(hidden = true)
    private String url;
    @ApiModelProperty(hidden = true)
    private String user;
    public User getUser() {
        JSONObject jsonObject = JSONObject.parseObject(user);
        return JSONObject.toJavaObject(jsonObject, User.class);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
