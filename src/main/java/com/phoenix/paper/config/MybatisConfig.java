//package com.phoenix.paper.config;
//import com.phoenix.paper.util.VersionInterceptor;
//import org.apache.ibatis.plugin.Interceptor;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * mybatis的配置:<br/>
// * <p>在配置plugin时,定义的越靠后则越先执行.<p/>
// */
//@Configuration
//@MapperScan("com.phoenix.paper.mapper")
//public class MybatisConfig {
//
//    @Bean
//    public Interceptor VersionInterceptor(){
//        return new VersionInterceptor();
//    }
//
//}