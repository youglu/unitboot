package org.union.sbp.springdemo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.union.sbp.springfragment.config.UnitBeanDefinConfiguration;

import javax.sql.DataSource;
// import org.union.sbp.springbase.adaptor.annoatation.UnitConfigComponent;


// @EnableConfigurationProperties(value = {DataSourceProperties.class,MybatisProperties.class})
@ComponentScan(value = {"org.union.sbp.springdemo.**"}, nameGenerator = UnitBeanDefinConfiguration.class
        ,basePackageClasses = {
        JdbcTemplateAutoConfiguration.class
        , MailSenderAutoConfiguration.class
        , DataSourceAutoConfiguration.class
}
)
@MapperScan(basePackages = {"org.union.sbp.springdemo.dao"})
@Import({Knife4jConfiguration.class})
@Configuration
// 当前如果不增加 @ConditionalOnBean(DataSource.class) 则无法初始化sessionfactory，导致mybatis无法使用，启动失败. 2024-02-08 22:05
@ConditionalOnBean(DataSource.class)
public class UnitConfiguration  {
    public UnitConfiguration(){
        System.out.println("初始化spring单元配置类v3");
    }
}
