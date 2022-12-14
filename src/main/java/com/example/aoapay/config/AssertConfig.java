package com.example.aoapay.config;


import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AssertConfig implements WebMvcConfigurer {
//    @Bean
//    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
//        return new OrderedHiddenHttpMethodFilter();
//    }
//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer){
//        WebMvcConfigurer.super.configureDefaultServletHandling(configurer);
//    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/admin/**").addResourceLocations( "classpath:/web/");
        registry.addResourceHandler("**").addResourceLocations( "classpath:/static/");
        //从这里开始，是我加的swagger的静态资源
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT","OPTIONS")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        List<String> exclude = new ArrayList<>();
//        exclude.add("/fonts/**");
//        exclude.add("/static/**");
//        exclude.add("/css/**");
//        exclude.add("/js/**");
//        exclude.add("/img/**");
//        exclude.add("/*");
//        exclude.add("/api/*");
//        exclude.add("/**");
//        AuthInterceptor authInterceptor = new AuthInterceptor();
//        registry.addInterceptor(authInterceptor).addPathPatterns("/**")
//                .excludePathPatterns(exclude)
//                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
    }
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomMethodArgumentResolver());
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}
