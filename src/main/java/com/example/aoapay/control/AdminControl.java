package com.example.aoapay.control;

import com.example.aoapay.data.ResponseData;
import com.example.aoapay.data.pData;
import com.example.aoapay.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/login")
    public ResponseData login(@ModelAttribute pData data, HttpServletRequest request){
        return service.login(data.getUsername(),data.getPassword(),request);
    }
    @GetMapping("/basicOrder/list")
    public ResponseData basicOrderList(
            @RequestParam(value="title", required = false) String title,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit",defaultValue = "1") int limit,
            HttpServletRequest request
    ){
        return service.basicOrderList(title,page,limit,request);
    }
}
