package com.example.aoapay.config;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Global {
    public static ExecutorService executor = Executors.newFixedThreadPool(6);
    public static ThreadPoolExecutor pool = (ThreadPoolExecutor) executor;
    private static String getPath(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null){
            return null;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        return request.getServletPath();
    }
}
