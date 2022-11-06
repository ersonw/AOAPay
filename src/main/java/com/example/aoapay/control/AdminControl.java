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
            @RequestParam(value = "limit",defaultValue = "10") int limit,
            HttpServletRequest request
    ){
        return service.basicOrderList(title,page,limit,request);
    }
    @GetMapping("/completedOrder/list")
    public ResponseData completedOrderList(
            @RequestParam(value="title", required = false) String title,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit",defaultValue = "10") int limit,
            HttpServletRequest request
    ){
        return service.completedOrderList(title,page,limit,request);
    }
    @GetMapping("/processedOrder/list")
    public ResponseData processedOrderList(
            @RequestParam(value="title", required = false) String title,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit",defaultValue = "10") int limit,
            HttpServletRequest request
    ){
        return service.processedOrderList(title,page,limit,request);
    }
    @PostMapping("/basicOrder/confirm")
    public ResponseData basicOrderConfirm(@ModelAttribute pData data, HttpServletRequest request){
        return service.basicOrderConfirm(data.getId(),request);
    }
    @GetMapping("/channel/list")
    public ResponseData channelList(
            @RequestParam(value="title", required = false) String title,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit",defaultValue = "10") int limit,
            HttpServletRequest request
    ){
        return service.channelList(title,page,limit,request);
    }
    @GetMapping("/channel/enable")
    public ResponseData channelEnable(
            @RequestParam(value="id", required = false) String id,
            HttpServletRequest request
    ){
        return service.channelEnable(id,request);
    }
    @PostMapping("/channel/enable")
    public ResponseData channelEnableAll(
            @ModelAttribute pData data,
            HttpServletRequest request
    ){
        return service.channelEnableAll(data.getIds(),request);
    }
    @PostMapping("/channel/change")
    public ResponseData channelChange(
            @ModelAttribute pData data,
            HttpServletRequest request
    ){
        return service.channelChange(data.getId(),data.getType(),data.getTitle(),
                data.getDomain(),data.getMchId(),data.getCallbackUrl(),
                data.getNotifyUrl(),data.getSecretKey(),data.isVoluntarily(),
                data.getChannel(),data.getMax(),data.getMini(),data.getSort(),
                data.getLimit(),data.getTypeCode(),data.getAmountList(),request);
    }
    @PostMapping("/channel/add")
    public ResponseData channelAdd(
            @ModelAttribute pData data,
            HttpServletRequest request
    ){
        return service.channelAdd(data.getType(),data.getTitle(),
                data.getDomain(),data.getMchId(),data.getCallbackUrl(),
                data.getNotifyUrl(),data.getSecretKey(),data.isVoluntarily(),
                data.getChannel(),data.getMax(),data.getMini(),data.getSort(),
                data.getLimit(),data.getTypeCode(),data.getAmountList(),request);
    }
    @PostMapping("/channel/remove")
    public ResponseData channelRemoveAll(
            @ModelAttribute pData data,
            HttpServletRequest request
    ){
        return service.channelRemoveAll(data.getIds(),request);
    }
}
