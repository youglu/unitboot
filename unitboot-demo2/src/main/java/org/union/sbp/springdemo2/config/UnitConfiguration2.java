package org.union.sbp.springdemo2.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author tianwen.yin
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan({"org.union.sbp.springdemo2.**"})
public class UnitConfiguration2 {

    public UnitConfiguration2(){
        System.out.println("初始化spring单元配置类v3");
    }

}