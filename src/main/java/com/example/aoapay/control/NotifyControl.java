package com.example.aoapay.control;

import com.example.aoapay.data.DandelionNotify;
import com.example.aoapay.data.EBoNotify;
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
    public String eBo(@ModelAttribute EBoNotify data, HttpServletRequest request){
        return service.eBo(data,request);
    }
    @PostMapping("/dandelion")
    public String dandelion(@ModelAttribute DandelionNotify data, HttpServletRequest request){
        return service.dandelion(data,request);
    }
}
