package org.union.sbp.springdemo.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.union.sbp.springbase.adaptor.annoatation.UnitConfigComponent;


@EnableConfigurationProperties(DataSourceProperties.class)
@ComponentScan(value = {"org.union.sbp.springdemo.**"})
@UnitConfigComponent
public class UnitConfiguration  {

    public UnitConfiguration(){
        System.out.println("初始化spring单元配置类v3");
    }
}