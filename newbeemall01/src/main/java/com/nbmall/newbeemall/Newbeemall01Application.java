package com.nbmall.newbeemall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.nbmall.newbeemall.dao")
@SpringBootApplication
public class Newbeemall01Application {

    public static void main(String[] args) {
        SpringApplication.run(Newbeemall01Application.class, args);
    }

}
