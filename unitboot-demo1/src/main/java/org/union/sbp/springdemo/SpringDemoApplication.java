package org.union.sbp.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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