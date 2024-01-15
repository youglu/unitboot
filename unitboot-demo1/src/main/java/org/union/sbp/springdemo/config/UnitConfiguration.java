package org.union.sbp.springdemo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
// import org.union.sbp.springbase.adaptor.annoatation.UnitConfigComponent;


@EnableConfigurationProperties(DataSourceProperties.class)
@ComponentScan(value = {"org.union.sbp.springdemo.controller.**"}, nameGenerator = DefaultBeanNameGenerator.class
        ,basePackageClasses = {
        JdbcTemplateAutoConfiguration.class
        , MailSenderAutoConfiguration.class
        , DataSourceAutoConfiguration.class
}
)

@MapperScan(basePackages = {"org.union.sbp.springdemo.dao"})
@Import({MybatisAutoConfiguration.class,Knife4jConfiguration.class})
// @UnitConfigComponent
public class UnitConfiguration  {
    public UnitConfiguration(){
        org.h2.upgrade.DbUpgrade gg = null;
        System.out.println("初始化spring单元配置类v3");
    }
}