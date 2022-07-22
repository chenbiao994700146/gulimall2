package com.cb.gulimall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class GuliFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //1、RequestContextHolder拿到刚进来的这个请求
                System.out.println("requestInterceptor线程。。。。" + Thread.currentThread().getId());

                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();//老请求
                    //2、同步请求头数据，Cookie
                    if (request != null) {
                        String cookie = request.getHeader("Cookie");
                        template.header("Cookie", cookie);

                        System.out.println("feign远程之前先进行requestInterceptor.apply方法");
                    }
                }


            }
        };
    }
}
