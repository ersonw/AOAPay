package com.example.aoapay.control;

import com.example.aoapay.service.ApiService;
import com.example.aoapay.service.ShortLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ShortLinkControl {
    @Autowired
    private ShortLinkService service;
    @Autowired
    private ApiService apiService;

    @GetMapping("/s/{id}")
    private void link(@PathVariable String id, HttpServletRequest request, HttpServletResponse response){
        service.link(id, request,response);
    }
    @GetMapping("/payment/{id}")
    private ModelAndView payment(@PathVariable String id, HttpServletRequest request){
        return apiService.payment(id, request);
    }
}
