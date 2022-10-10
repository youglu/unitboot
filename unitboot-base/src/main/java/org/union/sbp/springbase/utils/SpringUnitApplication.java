package org.union.sbp.springbase.utils;

import org.osgi.framework.Bundle;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.union.sbp.springbase.config.UnitBeanDefinitionRegistryPostProcessor;
import org.union.sbp.springbase.io.UnitResourceLoader;

import java.util.Map;

/**
 * 单元子SpringContext扫描与启动.
 * @deprecated 暂时没用到，但里面的代码可以在手动扫描时参考.
 */
public class SpringUnitApplication {

    /**
     * 启动并扫描子context.
     * @param context
     * @param source
     */
    public static void start(final Bundle unitBundle,final ConfigurableApplicationContext context, final Object[] source) {
        final ClassLoader resourceClassLoader = ( ClassLoader)source[0];
        final ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(getBeanDefinitionRegistry(context));
        final ResourceLoader resourceLoader = new UnitResourceLoader(resourceClassLoader);
        scanner.setResourceLoader(resourceLoader);
        String activatorClassName = unitBundle.getHeaders().get("Bundle-Activator");
        // 获得与启动器同级的包名.
        String scanPagePath = activatorClassName.substring(0,activatorClassName.lastIndexOf("."));
        scanner.scan(scanPagePath);//"org.union.sbp.springdemo.**");
/*
       这里可以指定配置类重新进行扫描配置。
        try {
            ((AnnotationConfigApplicationContext) context).setClassLoader(resourceClassLoader);
            final Class unitConfigurationClass = resourceClassLoader.loadClass("org.union.sbp.springdemo.config.UnitConfiguration");
            ((AnnotationConfigApplicationContext) context).register(unitConfigurationClass);
            UnitBeanDefinitionRegistryPostProcessor beanDefinitionRegistry = context.getBean(UnitBeanDefinitionRegistryPostProcessor.class);
            beanDefinitionRegistry.processConfigBeanDefinitions(beanDefinitionRegistry.getBeanDefinitionRegistry());

        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    /**
     * 根据context获得Bean注册器.
     * @param context
     * @return
     */
    private static BeanDefinitionRegistry getBeanDefinitionRegistry(ApplicationContext context) {
        if (context instanceof BeanDefinitionRegistry) {
            return (BeanDefinitionRegistry) context;
        } else if (context instanceof AbstractApplicationContext) {
            return (BeanDefinitionRegistry) ((AbstractApplicationContext) context).getBeanFactory();
        } else {
            throw new IllegalStateException("Could not locate BeanDefinitionRegistry");
        }
    }


}
