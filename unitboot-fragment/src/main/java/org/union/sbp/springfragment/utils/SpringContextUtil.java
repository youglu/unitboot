package org.union.sbp.springfragment.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 *  主单元的spring ApplicationContext 工具类，同时具备设置ServletContext对象的方法。
 * @author youg
 * @since JDK1.8
 *
 */
@Component
public class SpringContextUtil implements ApplicationContextAware, WebApplicationInitializer {
    /**
     * 上下文对象实例.
     */
    private static ApplicationContext applicationContext;
    /**
     * ServletContext对象.
     */
    private static ServletContext servletContext;

    /**
     * 设置ApplicationContext
     * @param applicationContext
     * @throws BeansException
     */
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取applicationContext
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
  
    /**
     * 通过name获取 Bean.
     * @param name
     * @return
     */
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }
  
    /**
     * 通过class获取Bean.
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }
  
    /**
     * 通过name,以及Clazz返回指定的Bean
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * web启动时发出的事件用于设置ServletContext
     * @param servletContext
     * @throws ServletException
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        this.servletContext = servletContext;
    }

    /**
     * 获得ServletContext
     * @return ServletContext
     */
    public static ServletContext getServletContext() {
        if(null == servletContext && null != applicationContext){
            servletContext = ((AnnotationConfigServletWebServerApplicationContext)applicationContext).getServletContext();
        }
        return servletContext;
    }
}