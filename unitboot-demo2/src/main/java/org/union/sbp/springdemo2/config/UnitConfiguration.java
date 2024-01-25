package org.union.sbp.springdemo2.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.union.sbp.springfragment.config.UnitBeanDefinConfiguration;


@EnableConfigurationProperties(DataSourceProperties.class)
@ComponentScan(value = {"org.union.sbp.springdemo2.**"}, nameGenerator = UnitBeanDefinConfiguration.class
        ,basePackageClasses = {
        JdbcTemplateAutoConfiguration.class
        , MailSenderAutoConfiguration.class
        , DataSourceAutoConfiguration.class
}
)
public class UnitConfiguration  {
    public UnitConfiguration(){
        System.out.println("初始化spring单元配置类v3");
    }
}