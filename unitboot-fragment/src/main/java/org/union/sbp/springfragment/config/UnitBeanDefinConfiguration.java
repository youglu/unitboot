package org.union.sbp.springfragment.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Configuration;

/**
 * spring子单元web配置.
 * @author youg
 * @since JDK1.8
 */
@Configuration
public class UnitBeanDefinConfiguration extends AnnotationBeanNameGenerator {
    public UnitBeanDefinConfiguration(){
        System.out.println("初始化UnitBeanDefinConfiguration");
    }

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        return super.generateBeanName(definition, registry);
    }

}