package com.atguigu.yygh.order.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "weipay")
@PropertySource("classpath:weipay.properties")
@Component
public class WeiPayProperties {
    private String appid;
    private String partner;
    private String partnerkey;
}
