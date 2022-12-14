package org.union.sbp.springbase.adaptor.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 用于所有子单元的公共配置，在子单元创建子context时会统一应用此类的注解配置
 * @author youg
 * @since JDK1.8
 */
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {EmbeddedDataSourceConfiguration.class}),
        @ComponentScan.Filter(value={SpringBootApplication.class})
        })
public class SpringBaseConfiguration {
    public SpringBaseConfiguration(){
        System.out.println("SpringBaseConfiguration 初始化");
    }

    /**
     * 由于默认会共享到主context的DataSourceProperties bean,
     * 而主DataSourceProperties是不会应用子context的配置文件，所以
     * 需新定义一个子context使用的DataSourceProperties bean.
     * @return
     */
    @Bean("unitDataSourceProperties")
    @Primary
    public DataSourceProperties unitDataSourceProperties(){
        return new DataSourceProperties();
    }
}

