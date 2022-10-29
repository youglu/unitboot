package org.union.sbp.springdemo2.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.union.sbp.springdemo2.SpringDemo2Application;

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