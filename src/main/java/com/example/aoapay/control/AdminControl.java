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
    public ResponseData menu(HttpServletRequest request) {
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
    @PostMapping("/basicOrder/clean")
    public ResponseData basicOrderClean(HttpServletRequest request){
        return service.basicOrderClean(request);
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
    @GetMapping("/user/list")
    public ResponseData userList(
            @RequestParam(value="title", required = false) String title,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit",defaultValue = "10") int limit,
            HttpServletRequest request
    ){
        return service.userList(title,page,limit,request);
    }
    @GetMapping("/user/enable")
    public ResponseData userEnable(
            @RequestParam(value="id", required = false) String id,
            HttpServletRequest request
    ){
        return service.userEnable(id,request);
    }
    @GetMapping("/user/change")
    public ResponseData userChange(
            @RequestParam(value="id", required = false) String id,
            HttpServletRequest request
    ){
        return service.userChange(id,request);
    }
    @PostMapping("/user/add")
    public ResponseData userAdd(
            @ModelAttribute pData data,
            HttpServletRequest request
    ){
        return service.userAdd(data.getUsername(),data.isAdmin(),data.isSuperAdmin(),data.isEnabled(),data.getRolesId(), data.getRemark(), request);
    }
    @PostMapping("/user/remove")
    public ResponseData userRemoveAll(
            @ModelAttribute pData data,
            HttpServletRequest request
    ){
        return service.userRemoveAll(data.getIds(),request);
    }
    @GetMapping("/router/list")
    public ResponseData routerList(
            @RequestParam(value="title", required = false) String title,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit",defaultValue = "10") int limit,
            HttpServletRequest request
    ){
        return service.routerList(title,page,limit,request);
    }
    @GetMapping("/client/list")
    public ResponseData clientList(
            @RequestParam(value="title", required = false) String title,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit",defaultValue = "10") int limit,
            HttpServletRequest request
    ){
        return service.clientList(title,page,limit,request);
    }
    @GetMapping("/client/remove")
    public ResponseData clientRemove(
            @RequestParam(value="id",required = true) String id,
            HttpServletRequest request
    ){
        return service.clientRemove(id,request);
    }
    @PostMapping("/client/remove")
    public ResponseData clientRemoveAll(
            @ModelAttribute pData data,
            HttpServletRequest request
    ){
        return service.clientRemoveAll(data.getIds(),request);
    }
    @GetMapping("/client/add")
    public ResponseData clientAdd(
            HttpServletRequest request
    ){
        return service.clientAdd(request);
    }
    @GetMapping("/shortLink/list")
    public ResponseData shortLinkList(
            @RequestParam(value="title", required = false) String title,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit",defaultValue = "10") int limit,
            HttpServletRequest request
    ){
        return service.shortLinkList(title,page,limit,request);
    }
    @GetMapping("/shortLink/remove")
    public ResponseData shortLinkRemove(
            @RequestParam(value="id",required = true) String id,
            HttpServletRequest request
    ){
        return service.shortLinkRemove(id,request);
    }
    @PostMapping("/shortLink/remove")
    public ResponseData shortLinkRemoveAll(
            @ModelAttribute pData data,
            HttpServletRequest request
    ){
        return service.shortLinkRemoveAll(data.getIds(),request);
    }
}
