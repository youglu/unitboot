package org.union.sbp.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.union.sbp.springfragment.adaptor.UnitApplicationContext;

@SpringBootApplication()
public class SpringDemoOsgiApplication {
    public static void run(String[] args) {
        SpringApplication springApplication = new SpringApplication(SpringDemoOsgiApplication.class);
        springApplication.setApplicationContextClass(UnitApplicationContext.class);
        springApplication.run(args);
    }
}

