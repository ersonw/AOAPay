package com.example.aoapay.control;

import com.example.aoapay.data.EBoData;
import com.example.aoapay.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notify")
public class NotifyControl {
    @Autowired
    private NotifyService service;
    @PostMapping("/eBo")
    public String eBo(@ModelAttribute EBoData data, HttpServletRequest request){
        return service.eBo(data,request);
    }
}
