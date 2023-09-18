package com.atguigu.yygh.cmn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.atguigu"})
@MapperScan("com.atguigu.yygh.cmn.mapper")
@EnableDiscoveryClient
public class ServiceDictMainStart {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(ServiceDictMainStart.class, args);
    }
}
