package com.example.aoapay.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.util.AESUtils;
import com.example.aoapay.util.ToolsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class CustomHttpServletRequest extends HttpServletRequestWrapper {

    private Map<String, String[]> params = new HashMap<>();
    private String body;
    private Map<String,String> headers=new HashMap<>();

    public CustomHttpServletRequest(HttpServletRequest request){
        super(request);
        this.params.putAll(request.getParameterMap());
        this.body = ToolsUtil.getRequestBody(request);
        if (body == null) return;
        if (StringUtils.isNotEmpty(request.getContentType()) && request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)){
            String decode =  AESUtils.Decrypt(this.body);
            if (decode != null){
                this.body = decode;
            }
            JSONObject object = JSONObject.parseObject(this.body);
            for (String key : object.keySet()) {
                if (object.get(key) != null){
                    this.addParameter(key, object.get(key));
                }
            }
        }
    }
    public void addHeader(String name,String value){
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String value=super.getHeader(name);

        if (headers.containsKey(name)){
            value=headers.get(name);
        }

        return value;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names= Collections.list(super.getHeaderNames());
        names.addAll(headers.keySet());

        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> list= Collections.list(super.getHeaders(name));

        if (headers.containsKey(name)){
            list.add(headers.get(name));
        }

        return Collections.enumeration(list);
    }
    /**
     * GET重载一个构造方法
     *
     * @param request
     * @param extendParams
     */
    public CustomHttpServletRequest(HttpServletRequest request, Map<String, String[]> extendParams) throws IOException {
        this(request);
        addAllParameters(extendParams);
        this.body = ToolsUtil.getRequestBody(request);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }

    public void addAllParameters(Map<String, String[]> otherParams) {
        for (Map.Entry<String, String[]> entry : otherParams.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
    }


    public void addParameter(String name, Object value) {
        if (value != null) {
            if (value instanceof String[]) {
                params.put(name, (String[]) value);
            } else if (value instanceof String) {
                params.put(name, new String[]{(String) value});
            } else {
                params.put(name, new String[]{String.valueOf(value)});
            }
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.params;
    }

    /**
     * POST
     * @return
     * @throws IOException
     */
    public CustomHttpServletRequest(HttpServletRequest request, String context) {
        super(request);
        this.params.putAll(request.getParameterMap());
        body = context;
    }
    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes("UTF-8"));
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }
        };
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
