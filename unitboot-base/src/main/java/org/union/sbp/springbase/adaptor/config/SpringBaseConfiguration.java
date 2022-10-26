package org.union.sbp.springbase.adaptor.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {EmbeddedDataSourceConfiguration.class})})
public class SpringBaseConfiguration {
    public SpringBaseConfiguration(){
        System.out.println("SpringBaseConfiguration 初始化");
    }
    @Bean("unitDataSourceProperties")
    @Primary
    public DataSourceProperties unitDataSourceProperties(){
        return new DataSourceProperties();
    }
}

