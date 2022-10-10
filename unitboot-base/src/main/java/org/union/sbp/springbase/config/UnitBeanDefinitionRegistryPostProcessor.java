package org.union.sbp.springbase.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.union.sbp.springbase.io.UnitResourceLoader;

import java.util.Map;

public class UnitBeanDefinitionRegistryPostProcessor extends ConfigurationClassPostProcessor implements ApplicationContextAware {
    private BeanDefinitionRegistry beanDefinitionRegistry = null;
    public UnitBeanDefinitionRegistryPostProcessor(){
        System.out.println("初始化UnitBeanDefinitionRegistryPostProcessor");
    }
    public BeanDefinitionRegistry getBeanDefinitionRegistry(){
        return beanDefinitionRegistry;
    }
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ConfigurationClassPostProcessor> beans = applicationContext.getBeansOfType(ConfigurationClassPostProcessor.class);
        beans.forEach((key,val)->{
            val.setResourceLoader(new UnitResourceLoader(applicationContext.getClassLoader()));
            val.setBeanClassLoader(Thread.currentThread().getContextClassLoader());
        });
    }
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        super.postProcessBeanDefinitionRegistry(beanDefinitionRegistry);
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }
}
