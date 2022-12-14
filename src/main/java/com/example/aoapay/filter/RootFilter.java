package com.example.aoapay.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.dao.ClientDao;
import com.example.aoapay.table.Client;
import com.example.aoapay.util.ToolsUtil;
import com.example.aoapay.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TimeZone;

@WebFilter(filterName = "rootFilter", urlPatterns = {"/api/*","/payment/*"})
@Slf4j
public class RootFilter implements Filter {
    private ClientDao clientDao;
    @Override
    public void init(FilterConfig filterConfig){
        clientDao = Utils.getClientDao();
//        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.of("+8")));
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        log.info("Filter rootFilter success");
    }
    public String getCookie(Cookie[] cookies){
        if (cookies !=null){
            for (Cookie cookie: cookies){
                if (StringUtils.equals(cookie.getName(), "clientId")){
                    if (StringUtils.isNotEmpty(cookie.getValue())) return cookie.getValue();
                }
            }
        }
        return null;
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            CustomHttpServletRequest request = new CustomHttpServletRequest((HttpServletRequest) req);
            Cookie[] cookies = request.getCookies();
            String token = getCookie(cookies);
            Client client = clientDao.findById(token);
            if (client == null){
                client = Utils.addClient(request,(HttpServletResponse)response);
            }else{
                client.setUpdateHeader(JSONObject.toJSONString(ToolsUtil.getRequestHeaders(request)));
            }
            clientDao.save(client);
            Utils.clearClient(cookies,token, (HttpServletResponse) response);
            request.addHeader("client", JSONObject.toJSONString(client));
            chain.doFilter(request, response);
        } else {
            chain.doFilter(req, response);
        }
    }
    @Override
    public void destroy() {

    }
}
