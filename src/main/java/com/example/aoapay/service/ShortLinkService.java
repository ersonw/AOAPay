package com.example.aoapay.service;

import com.alibaba.fastjson.JSONObject;
import com.example.aoapay.dao.ClientDao;
import com.example.aoapay.dao.ConfigDao;
import com.example.aoapay.dao.ShortLinkDao;
import com.example.aoapay.dao.ShortLinkRecordDao;
import com.example.aoapay.data.RequestHeader;
import com.example.aoapay.table.Client;
import com.example.aoapay.table.Config;
import com.example.aoapay.table.ShortLink;
import com.example.aoapay.table.ShortLinkRecord;
import com.example.aoapay.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Slf4j
public class ShortLinkService {
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private ShortLinkDao shortLinkDao;
    @Autowired
    private ShortLinkRecordDao shortLinkRecordDao;
    public void link(String id, HttpServletRequest request, HttpServletResponse response) {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        ShortLink link = shortLinkDao.findByShortLink(id);
//        System.out.println(link);
        try{
            if (link==null) {
                response.sendError(404);
                return;
            }
            if (StringUtils.isEmpty(link.getUrl())){
                link.setUrl("/");
            }
            if (StringUtils.isEmpty(link.getClientId())){
                if (StringUtils.isEmpty(link.getUserId())){
                    response.sendError(403);
                    return;
                }
                Client client = new Client(JSONObject.toJSONString(header));
                clientDao.save(client);
                link.setClientId(client.getId());
                shortLinkDao.save(link);
            }
            shortLinkRecordDao.save(new ShortLinkRecord(link.getId(), JSONObject.toJSONString(header)));
//            response.addCookie(new Cookie("clientId", ""));
            Cookie cookie = new Cookie("clientId", link.getClientId());
            cookie.setPath("/");
//            cookie.setDomain("");
            response.addCookie(cookie);
            response.sendRedirect(link.getUrl());
        }catch(Exception e){
            log.info(e.getMessage());
        }
    }
    public String add(String userId, String url){
        List<Config> configs = configDao.findAll();
        if (configs.size() > 0){
            ShortLink link = new ShortLink(userId,null);
            link.setUrl(url);
            shortLinkDao.save(link);
            String hostname = configs.get(0).getHostname();
            if (StringUtils.isEmpty(hostname)) hostname = "/";
            if (!hostname.startsWith("http")) hostname = "http://" + hostname;
            if (!hostname.endsWith("/")) hostname +="/";
            return hostname+link.getShortLink();
        }
        return null;
    }
    public String getClient(String clientId, String url){
        List<Config> configs = configDao.findAll();
        if (configs.size() > 0){
            ShortLink link = new ShortLink(null,clientId);
            link.setUrl(url);
            shortLinkDao.save(link);
            String hostname = configs.get(0).getHostname();
            if (StringUtils.isEmpty(hostname)) {
                hostname = "/";
            }else{
                if (!hostname.startsWith("http")) hostname = "http://" + hostname;
                if (!hostname.endsWith("/")) hostname +="/";
            }
            return hostname+link.getShortLink();
        }
        return null;
    }

    public Object greaterLink(String clientId) {
        List<Config> configs = configDao.findAll();
        if (configs.size() > 0){
            ShortLink link = shortLinkDao.findByClient(clientId);
            if (link == null){
                link = new ShortLink(null,clientId);
                shortLinkDao.save(link);
            }
            String hostname = configs.get(0).getHostname();
            if (StringUtils.isEmpty(hostname)) {
                hostname = "/";
            }else{
                if (!hostname.startsWith("http")) hostname = "http://" + hostname;
                if (!hostname.endsWith("/")) hostname +="/";
            }
            return hostname+"s/"+link.getShortLink();
        }
        return null;
    }
}
