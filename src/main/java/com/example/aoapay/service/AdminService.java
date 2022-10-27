package com.example.aoapay.service;

import com.example.aoapay.dao.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class AdminService {
    @Autowired
    private AuthDao authDao;
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private PayListDao payListDao;
    @Autowired
    private RoleListDao roleListDao;
    @Autowired
    private RolesListDao rolesListDao;
    @Autowired
    private RouterListDao routerListDao;
    @Autowired
    private ShortLinkDao shortLinkDao;
    @Autowired
    private ShortLinkRecordDao shortLinkRecordDao;
    @Autowired
    private UserDao userDao;


    public String menu(HttpServletRequest request) {
    }
}
