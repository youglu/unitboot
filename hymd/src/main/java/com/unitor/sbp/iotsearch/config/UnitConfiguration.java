package com.unitor.sbp.iotsearch.config;

import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author tianwen.yin
 */
@Configuration
@ComponentScan(
        //excludeFilters = {
               // @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {EmbeddedDataSourceConfiguration.class})
       // },
        basePackageClasses = {JdbcTemplateAutoConfiguration.class, MailSenderAutoConfiguration.class,DataSourceAutoConfiguration.class},
        value = {"com.alibaba.druid.spring.boot.autoconfigure","com.unitor.sbp.iotsearch.**"})
@EnableAutoConfiguration
@MapperScan(basePackages = {"com.unitor.sbp.iotsearch.dao"})
// @ConditionalOnProperty(name="unitboot",havingValue = "true")
//@ConditionalOnClass(HikariDataSource.class)
public class UnitConfiguration {

    @Autowired
    Environment environment;
    public UnitConfiguration(){
        System.out.println("初始化hymd配置类");
    }
    @PostConstruct
    public void init(){
        System.out.println(environment.getProperty("uname"));
    }

}