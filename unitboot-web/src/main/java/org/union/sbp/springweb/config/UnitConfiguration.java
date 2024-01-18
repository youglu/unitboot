package org.union.sbp.springweb.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * web单元配置类
 * @author youg
 */
@Configuration
public class UnitConfiguration implements ApplicationContextAware {


    private static ApplicationContext springContext;

    public UnitConfiguration(){
        System.out.println("初始化spring web单元配置类");
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        setSpringContext(applicationContext);
    }
    private static void setSpringContext(final ApplicationContext applicationContext){
        springContext = applicationContext;
    }
    public static ApplicationContext getApplicationContext(){
        return springContext;
    }

   // @Bean
    //public HomeController getHomeController(){
      //  return new HomeController();
    //}

}