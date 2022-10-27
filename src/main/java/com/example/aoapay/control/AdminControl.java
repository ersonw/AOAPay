package com.example.aoapay.control;

import com.example.aoapay.data.ResponseData;
import com.example.aoapay.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/admin/api")
public class AdminControl {
    @Autowired
    private AdminService service;
    @GetMapping("/menu")
    public String menu(HttpServletRequest request) {
        return service.menu(request);
    }
}
