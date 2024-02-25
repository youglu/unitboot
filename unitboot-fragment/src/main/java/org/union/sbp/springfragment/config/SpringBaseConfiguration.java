package org.union.sbp.springfragment.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 用于所有子单元的公共配置，在子单元创建子context时会统一应用此类的注解配置
 * @author youg
 * @since JDK1.8
 */
@ComponentScan(
        basePackages = {"org.union.sbp.**"}
    )
@Conditional(OnOsgiEnvCondition.class)
@Configuration
public class SpringBaseConfiguration {
    public SpringBaseConfiguration(){
        System.out.println("SpringBaseConfiguration 初始化");
    }


}

