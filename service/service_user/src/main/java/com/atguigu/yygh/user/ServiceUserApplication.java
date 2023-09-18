package com.atguigu.yygh.user;

import com.atguigu.yygh.user.prop.WeiXinProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.atguigu"})
@MapperScan("com.atguigu.yygh.user.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.atguigu")
@EnableConfigurationProperties(value = WeiXinProperties.class)
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}
