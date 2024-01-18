package org.union.sbp.springweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.union.sbp.springweb","org.union.sbp.springweb.**"})
public class SprinpWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(SprinpWebApplication.class, args);
    }
}