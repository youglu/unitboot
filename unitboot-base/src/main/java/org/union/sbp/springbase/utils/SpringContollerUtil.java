package org.union.sbp.springbase.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * spring单元contoller附加到主context中.
 * @author youg
 * @since jdk1.8
 */
public class SpringContollerUtil {
    private final static Logger log = LoggerFactory.getLogger(SpringContollerUtil.class);
    /**
     * 从主context解除注册指定controller bean.
     * @param controller
     * @return
     * @throws NoSuchMethodException
     */
    public static Map<Method, RequestMappingInfo> unRegisterControllerWithBean(final Object controller) throws NoSuchMethodException {
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        final Method getMappingForMethod = requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getMappingForMethod", Method.class,Class.class);
        //将private改为可使用
        getMappingForMethod.setAccessible(true);
        Class<?> userType = ClassUtils.getUserClass(controller.getClass());
        MethodIntrospector.MetadataLookup<RequestMappingInfo> metadataLookup = (method)->{
            try {
                return (RequestMappingInfo) getMappingForMethod.invoke(requestMappingHandlerMapping, new Object[]{method, userType});
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        };
        Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(userType, metadataLookup);
        methods.forEach((method, mapping) -> {
            UnitLogger.info(log,"解除注册mapping：{}",mapping);
            requestMappingHandlerMapping.unregisterMapping(mapping);
        });

        return methods;
    }

    /**
     * 将指定controller bean注册到主context。
     * @param controller
     */
    public static void registerControllerWithBean(final Object controller){
        final ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        final RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        try {
            // 先清除
            final Map<Method, RequestMappingInfo> methods = unRegisterControllerWithBean(controller);
            //final RequestMappingInfo requestMappingInfo = RequestMappingInfo
             //       .paths("test")
             //       .methods(RequestMethod.GET)
             //       .build();

            methods.forEach((method, mapping) -> {
                UnitLogger.info(log,"注册mapping：{}->{}",mapping,method);
                requestMappingHandlerMapping.registerMapping(mapping, controller, method);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 将指定controller bean注册到主context。
     * @param controller
     */
    public static void registerFilterWithBean(final Object controller){
        final ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        try {
            AnnotationConfigApplicationContext unitApplicationContext = (AnnotationConfigApplicationContext)applicationContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
