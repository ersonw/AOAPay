package com.example.aoapay.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.dao.AuthDao;
import com.example.aoapay.table.User;
import com.example.aoapay.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@WebFilter(filterName = "adminFilter", urlPatterns = {"/admin/api/*"})
public class AdminFilter implements Filter {
    private AuthDao authDao;
    @Override
    public void init(FilterConfig filterConfig){
        authDao = Utils.getAuthDao();
//        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.of("+8")));
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
//            HttpServletRequest request = (HttpServletRequest) req;
            CustomHttpServletRequest request = new CustomHttpServletRequest((HttpServletRequest) req);
            String contentType = request.getContentType();
            String token = request.getHeader("Token");
            if (StringUtils.isNotEmpty(token)){
                User user = authDao.findUserByToken(token);
                if (user != null)request.addHeader("user",JSONObject.toJSONString(user));
            }
            chain.doFilter(request, response);
        } else {
            chain.doFilter(req, response);
        }
    }
    @Override
    public void destroy() {

    }
}
