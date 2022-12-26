package com.phoenix.paper;

import com.phoenix.paper.single.ShuaiServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.phoenix.paper"})
@EnableCaching
@EnableScheduling
@MapperScan("com.phoenix.paper.mapper")
public class PaperApplication {

    public static void main(String[] args) {
        ShuaiServer.open();
        SpringApplication.run(PaperApplication.class, args);
    }

}
