package com.example.aoapay.control;

import com.example.aoapay.data.ResponseData;
import com.example.aoapay.data.pData;
import com.example.aoapay.service.ApiService;
import com.example.aoapay.util.MD5Util;
import com.example.aoapay.util.ToolsUtil;
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
    public ResponseData payList(@PathVariable("version") String version,
                                HttpServletRequest request, HttpServletResponse response) {
        return service.payList(version,request,response);
    }
    @GetMapping("/payList/order")
    public ResponseData payListOrder(@RequestParam(value="page",defaultValue="1") int page,
                                HttpServletRequest request) {
        return service.payListOrder(page,request);
    }
    @PostMapping("/payList/submit")
    public ResponseData payListSubmit(@ModelAttribute pData data,HttpServletRequest request){
        return service.payListSubmit(data.getName(),data.getUsername(),data.getPayListId(),data.getAmount(),request);
    }
    @GetMapping("/test")
    public String test(){
        service.test();
        return "ok";
    }
}
