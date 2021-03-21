package com.jit.jd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jit.jd.mapper")
public class JdApplication {

    public static void main(String[] args) {
        SpringApplication.run(JdApplication.class, args);
    }

}
