package com.phoenix.read.config;
import com.github.pagehelper.PageHelper;
import com.phoenix.read.util.VersionInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * mybatis的配置:<br/>
 * <p>在配置plugin时,定义的越靠后则越先执行.<p/>
 */
@Configuration
@MapperScan("com.phoenix.read.mapper")
public class MybatisConfig {

    @Bean
    public Interceptor VersionInterceptor(){
        return new VersionInterceptor();
    }

}