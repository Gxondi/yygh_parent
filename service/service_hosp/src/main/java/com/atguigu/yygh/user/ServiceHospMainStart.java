package com.atguigu.yygh.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.atguigu.yygh")
@MapperScan(value = "com.atguigu.yygh.user.mapper")
@EnableDiscoveryClient //@EnableEurekaClient
@EnableFeignClients(basePackages = "com.atguigu.yygh")
public class ServiceHospMainStart {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospMainStart.class, args);
    }
}
