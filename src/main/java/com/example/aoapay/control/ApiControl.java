package com.example.aoapay.control;

import com.example.aoapay.data.ResponseData;
import com.example.aoapay.service.ApiService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class ApiControl {
    @Autowired
    private ApiService service;
    @GetMapping("/payList/{version}")
    public void payList(@PathVariable("version") String version,
                                HttpServletRequest request, HttpServletResponse response) {
        service.payList(version,request,response);
    }
}
