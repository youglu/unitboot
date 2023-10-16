package org.union.sbp.springweb.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Properties;

/**
 * @author tianwen.yin
 */
@ComponentScan(value={"org.union.sbp.springdemo2.**"}, excludeFilters = {@ComponentScan.Filter(value={SpringBootApplication.class})})
public class UnitConfiguration2 {
    public UnitConfiguration2(){
        System.out.println("初始化spring单元配置类v3");
    }

    @Bean("unitProperties")
    public Properties sike(){
        return new Properties();
    }
}