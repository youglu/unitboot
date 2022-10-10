package org.union.sbp.springdemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author tianwen.yin
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(value = {"org.union.sbp.springdemo.**"})
public class UnitConfiguration {

    public UnitConfiguration(){
        System.out.println("初始化spring单元配置类v3");
    }
/*
    @Bean
    public HomeController homeController(){
        return new HomeController();
    }
    @Bean
    public DemoService demoService(){
        return new DemoService();
    }

    @Bean
   @ConditionalOnMissingBean
    public Knife4jConfiguration knife4jConfiguration(){
        return new Knife4jConfiguration();
    }

    @Bean(value = "defaultApi3")
    @ConditionalOnMissingBean(name="defaultApi3")
    public Docket defaultApi3(Knife4jConfiguration knife4jConfiguration) {
        return knife4jConfiguration.defaultApi3();
    }
    */
}