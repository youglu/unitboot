package org.union.sbp.springbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 独立springboot方式启动主类
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SpringBaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBaseApplication.class, args);
    }
}