package org.union.sbp.springfragment.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.DispatcherServlet;
// @Configuration
public class UnitBeanDefineRgigi   {

        @Bean
        @Primary
        public DispatcherServlet myDispatcherServlet() {
            return new DispatcherServlet();
        }

        @Bean
        public ServletRegistrationBean<DispatcherServlet> dispatcherServletRegistration() {
            ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<>(
                    myDispatcherServlet(), "/");
            registration.setName("myDispatcherServlet");
            return registration;
        }
}
