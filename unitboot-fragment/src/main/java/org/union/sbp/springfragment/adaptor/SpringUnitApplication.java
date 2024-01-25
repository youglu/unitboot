package org.union.sbp.springfragment.adaptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication; 

@SpringBootApplication()
public class SpringUnitApplication {
    public static void run(String[] args) {
        SpringApplication springApplication = new SpringApplication(SpringUnitApplication.class);
        springApplication.setApplicationContextClass(UnitApplicationContext.class);
        springApplication.run(args);
    }
}

