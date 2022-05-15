package com.phoenix.paper.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 接口文档，生产时关闭
 *
 * @author yan on 2020-02-27
 */
@Configuration
@EnableSwagger2
@ConditionalOnExpression("${dev.enable:true}")//当enable为true时才选择加载该配置类
public class Swagger2Config {

    private static final String BASE_PACKAGE1 = "com.phoenix.paper.controller";
    private static final String GROUP_NAME1 = "controller";
    private static final String BASE_PACKAGE2 = "com.phoenix.paper.entity";
    private static final String GROUP_NAME2 = "entity";
    private static final String BASE_PACKAGE3 = "com.phoenix.paper.dto";
    private static final String GROUP_NAME3 = "dto";
    private static final String TITLE = "paper API Documentation";
    private static final String DESCRIPTION = "论文管理系统";

    @Bean
    public Docket createControllerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(GROUP_NAME1)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE1))//设定扫描范围
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket createEntityApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(GROUP_NAME2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE2))//设定扫描范围
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket createDtoApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(GROUP_NAME3)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE3))//设定扫描范围
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(TITLE)
                .description(DESCRIPTION)
                //.termsOfServiceUrl("http://124.222.112.118:8081/swagger-ui.html")//数据源
                .version("1.0")
                .build();
    }
}
