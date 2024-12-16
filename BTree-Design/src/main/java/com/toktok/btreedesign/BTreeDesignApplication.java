package com.toktok.btreedesign;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.toktok.btreedesign.mapper")
public class BTreeDesignApplication {

    public static void main(String[] args) {
        SpringApplication.run(BTreeDesignApplication.class, args);
    }

}
