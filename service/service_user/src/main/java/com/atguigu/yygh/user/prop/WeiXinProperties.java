package com.atguigu.yygh.user.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "weixin")
public class WeiXinProperties {
    private String appid;
    private String appsecret;
    private String redirecturl;
}
