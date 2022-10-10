package org.union.sbp.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class SpringDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringDemoApplication.class, args);
    }
   /* @Configuration
    @ComponentScan({"org.union.sbp.springdemo.**","org.union.sbp.springbase.**"})
    public class SpringBaseConfiguration {
    }*/
}