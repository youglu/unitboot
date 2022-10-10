package org.union.sbp.springbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.union.sbp.springbase.config.Knife4jConfiguration;
import org.union.sbp.springbase.config.SpringBaseConfiguration;
import org.union.sbp.springbase.utils.SpringContextUtil;

@SpringBootApplication
public class SpringBaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBaseApplication.class, args);
    }
/*
    @Bean
    @ConditionalOnMissingBean
    public SpringContextUtil springContextUtil(){
        return new SpringContextUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringBaseConfiguration springBaseConfiguration(){
        return new SpringBaseConfiguration();
    }

    @Bean
    @ConditionalOnMissingBean
    public Knife4jConfiguration knife4jConfiguration(){
        return new Knife4jConfiguration();
    }
*/
}