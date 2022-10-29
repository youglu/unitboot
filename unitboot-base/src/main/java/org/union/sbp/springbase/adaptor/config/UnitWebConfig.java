package org.union.sbp.springbase.adaptor.config;

import org.springframework.boot.autoconfigure.http.HttpProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.union.sbp.springbase.adaptor.web.UnitDispatcherServlet;
import org.union.sbp.springbase.adaptor.web.swagger.UnitRequestHandlerProvider;
import springfox.documentation.spi.service.RequestHandlerProvider;

/**
 * spring子单元web配置.
 * @author youg
 * @since JDK1.8
 */
public class UnitWebConfig implements WebMvcConfigurer {

    public UnitWebConfig(){
        System.out.println("初始化UnitWebConfig");
    }

    /**
     * 子单元的DispatcherServlet bean，用于提供在更新单元后的DispatcherServlet刷新.
     * @param httpProperties
     * @param webMvcProperties
     * @return
     */
    @Bean(name = {"dispatcherServlet"})
    public DispatcherServlet dispatcherServlet(HttpProperties httpProperties, WebMvcProperties webMvcProperties) {
        DispatcherServlet dispatcherServlet = new UnitDispatcherServlet();
        dispatcherServlet.setDispatchOptionsRequest(webMvcProperties.isDispatchOptionsRequest());
        dispatcherServlet.setDispatchTraceRequest(webMvcProperties.isDispatchTraceRequest());
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(webMvcProperties.isThrowExceptionIfNoHandlerFound());
        dispatcherServlet.setPublishEvents(webMvcProperties.isPublishRequestHandledEvents());
        dispatcherServlet.setEnableLoggingRequestDetails(httpProperties.isLogRequestDetails());
        return dispatcherServlet;
    }
    /**
     * 提供单元的RequestMappingHandlerMapping,用于映射单元内的controler.
     * 已知的swagger会用到，而不只是取到主context的.
     * @return
     */
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
      RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();
      requestMappingHandlerMapping.setOrder(0);
      return requestMappingHandlerMapping;
    }

    /**
     * 为swagger提供单元的RequestHandlerProvider,用于子单元将子单元中的controller信息初始化到swagger.
     * @param requestMappingHandlerMapping
     * @return
     */
    @Bean
    public RequestHandlerProvider requestHandlerProvider(RequestMappingHandlerMapping requestMappingHandlerMapping){
        return new UnitRequestHandlerProvider(requestMappingHandlerMapping);
    }
}