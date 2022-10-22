package com.example.aoapay.service;

import com.example.aoapay.dao.ClientDao;
import com.example.aoapay.dao.PayListDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Service
public class ApiService {
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private PayListDao payListDao;
    public void payList(String version, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendError(200);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
