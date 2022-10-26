package org.union.sbp.springdemo.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author tianwen.yin
 */
@EnableConfigurationProperties(DataSourceProperties.class)
@ComponentScan(value = {"org.union.sbp.springdemo.**"})
public class UnitConfiguration  {

    public UnitConfiguration(){
        System.out.println("初始化spring单元配置类v3");
    }
}