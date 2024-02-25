package org.union.sbp.unitmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Knife4jConfiguration {

    public Knife4jConfiguration(){
        System.out.println("实始化Knife4jConfiguration...");
    }

    @Bean(value = "defaultApi4")
    public Docket defaultApi4() {
        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("demo2 API文档")
                        .description("# demo2 API文档")
                        .termsOfServiceUrl("http://www.xx.com/")
                        .contact(new Contact("unitboot","http://www.gddshl.com","unitboot@unitboot.com"))
                        .version("1.0")
                        .build())
                //分组名称
                .groupName("demo2")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("org.union.sbp.springdemo2"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }
}