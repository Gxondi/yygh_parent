package com.atguigu.yygh.common.config;

import com.google.common.base.Predicates;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootConfiguration
@EnableSwagger2 // 开启Swagger2的自动配置
public class SwaggerConfig {
    @Bean
    public Docket getAdminDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("admin")
                .apiInfo(getAdminApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }
    @Bean
    public ApiInfo getAdminApiInfo() {
        return new ApiInfoBuilder()
                .title("管理员系统使用")
                .description("商医通预约平台系统值管理员系统")
                .version("1.0")
               // .contact(new Contact("LH","http://www.baidu.com","123@123.com"))
                .build();
    }
    @Bean
    public Docket getUserDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("user")
                .apiInfo(getUserApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/user/.*")))
                .build();
    }
    @Bean
    public ApiInfo getUserApiInfo() {
        return new ApiInfoBuilder()
                .title("用户系统使用")
                .description("商医通预约平台系统用户系统")
                .version("1.0")
                // .contact(new Contact("LH","http://www.baidu.com","123@123.com"))
                .build();
    }
}
