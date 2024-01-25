package org.union.sbp.springfragment.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.union.sbp.springfragment.adaptor.UnitDispatcherServlet;
import org.union.sbp.springfragment.utils.SpringUnitUtil;
import org.union.sbp.springfragment.utils.UnitUtil;

import javax.servlet.MultipartConfigElement;

/**
 * 配置类
 */
@Configuration
@Conditional(OnOsgiEnvCondition.class)
public class UnitBeanDefineRegistration {

    /**日志*/
    protected final Logger logger = LoggerFactory.getLogger(UnitBeanDefineRegistration.class);

    /**
     * 子单元的DispatcherServlet bean，用于提供在更新单元后的DispatcherServlet刷新.
     * @param httpProperties
     * @param webMvcProperties
     * @return
     */
    @Bean(name = {"dispatcherServlet"})
    public DispatcherServlet dispatcherServlet(HttpProperties httpProperties, WebMvcProperties webMvcProperties) {
        UnitDispatcherServlet dispatcherServlet = new UnitDispatcherServlet();
        dispatcherServlet.setDispatchOptionsRequest(webMvcProperties.isDispatchOptionsRequest());
        dispatcherServlet.setDispatchTraceRequest(webMvcProperties.isDispatchTraceRequest());
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(webMvcProperties.isThrowExceptionIfNoHandlerFound());
        dispatcherServlet.setPublishEvents(webMvcProperties.isPublishRequestHandledEvents());
        dispatcherServlet.setEnableLoggingRequestDetails(httpProperties.isLogRequestDetails());

        // 注册到osgi环境
        UnitUtil.registService(dispatcherServlet,DispatcherServlet.class.getName());
        return dispatcherServlet;
    }
    /**
     * 覆盖dispatcherServlet的注册处理类,用于对注册进行重命名名，解决多个springboot单元共享同一个web容器无法注册dpatcherServlet到web容器问题.
     *
     * @param dispatcherServlet dispatcherServlet
     * @param webMvcProperties webMvcProperties
     * @param multipartConfig multipartConfig
     * @return DispatcherServletRegistrationBean
     */
    @ConditionalOnBean(value = DispatcherServlet.class)
    @Bean
    public DispatcherServletRegistrationBean dispatcherServletRegistration(DispatcherServlet dispatcherServlet,
                                                                           WebMvcProperties webMvcProperties, ObjectProvider<MultipartConfigElement> multipartConfig) {
        final String registPath = dispatcherServlet.getEnvironment().getProperty("server.servlet.context-path");
        // webMvcProperties.getServlet().getPath()
        DispatcherServletRegistrationBean registration = new DispatcherServletRegistrationBean(dispatcherServlet,
                registPath);
        // 重命名dispatcherServlet的注册名，解决多个springboot单元共享同一个web容器无法注册dpatcherServlet到web容器问题.
        registration.setName(SpringUnitUtil.getDispatcherServletRegistName());
        registration.setLoadOnStartup(webMvcProperties.getServlet().getLoadOnStartup());
        multipartConfig.ifAvailable(registration::setMultipartConfig);
        return registration;
    }

}
