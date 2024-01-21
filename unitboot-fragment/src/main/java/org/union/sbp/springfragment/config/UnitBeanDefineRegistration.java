package org.union.sbp.springfragment.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.MultipartConfigElement;

@Configuration
public class UnitBeanDefineRegistration {

        //@Bean
        //@Primary
        public DispatcherServlet unitDispatcherServlet() {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            return dispatcherServlet;
        }

       // @Bean
        public ServletRegistrationBean<DispatcherServlet> dispatcherServletRegistration() {
            ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<>(
                    unitDispatcherServlet(), "/");
            registration.setName("myDispatcherServlet");
            return registration;
        }
    /**
     * 提供单元的RequestMappingHandlerMapping,用于映射单元内的controler.
     * 已知的swagger会用到，而不只是取到主context的.
     * @return
     */
   // @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();
        requestMappingHandlerMapping.setOrder(0);
        return requestMappingHandlerMapping;
    }

    //@Bean
   // public DispatcherServletPath dispatcherServletPath(){
        //DispatcherServletPath dispatcherServletPath = new (dispatcherServletRegistration(),"/");
        //return dispatcherServletPath
   // }


    @ConditionalOnBean(value = DispatcherServlet.class)
    @Bean
    public DispatcherServletRegistrationBean dispatcherServletRegistration(DispatcherServlet dispatcherServlet,
                                                                           WebMvcProperties webMvcProperties, ObjectProvider<MultipartConfigElement> multipartConfig) {
        DispatcherServletRegistrationBean registration = new DispatcherServletRegistrationBean(dispatcherServlet,
                webMvcProperties.getServlet().getPath());
        registration.setName("myDispatcherServlet");
        registration.setLoadOnStartup(webMvcProperties.getServlet().getLoadOnStartup());
        multipartConfig.ifAvailable(registration::setMultipartConfig);
        return registration;
    }

}
