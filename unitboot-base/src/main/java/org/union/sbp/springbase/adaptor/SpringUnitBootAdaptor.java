package org.union.sbp.springbase.adaptor;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;
import org.union.sbp.springbase.adaptor.config.SpringBaseConfiguration;
import org.union.sbp.springbase.adaptor.config.UnitBeanDefinitionRegistryPostProcessor;
import org.union.sbp.springbase.adaptor.config.UnitWebConfig;
import org.union.sbp.springbase.adaptor.io.UnitResourceLoader;
import org.union.sbp.springbase.utils.SpringContextUtil;
import org.union.sbp.springbase.utils.SpringUnitUtil;
import org.union.sbp.springbase.utils.UnitLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * SpringBoot工程以OSGI单元形式运行.
 * @author youg
 * @since jdk1.8
 */
public class SpringUnitBootAdaptor {

    private final static Logger log = LoggerFactory.getLogger(SpringUnitBootAdaptor.class);
    /**
     * spring单元标识，只处理朋此标识的单元.
     */
    public final static String SRING_UNIT = "spring-unit";
    /**
     * 单元子命名spring context工厂,用于创建、保存、销毁单元的子spring context.
     */
    private static final UnitNamedFactory unitNamedFactory = new UnitNamedFactory(SpringBaseConfiguration.class , "application.xml", "");

    public static NamedContextFactory getUnitNamedFactory(){
        return unitNamedFactory;
    }
    /**
     * 单元安装处理.
     * @param springUnitBundle
     */
    public static void startSpringUnit(final Bundle springUnitBundle){
        final long startTime = System.currentTimeMillis();
        final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            //如果不是spring单元测不处理
            String  unitType = springUnitBundle.getHeaders().get("Unit-Type");
            if(StringUtils.isEmpty(unitType) || !SRING_UNIT.equals(unitType)){
                UnitLogger.info(log,"当前单元不是Spring单元，不进行Spring适配");
                return;
            }
            // 如果是当前单元则跳过.
            if(springUnitBundle.getSymbolicName().equals(FrameworkUtil.getBundle(SpringUnitUtil.class).getSymbolicName())){
                return;
            }
            //设置主springContext
            ApplicationContext rootApplicationContext = SpringContextUtil.getApplicationContext();
            unitNamedFactory.setApplicationContext(rootApplicationContext);
            // 创建单元子上下文
            final String contextName = springUnitBundle.getSymbolicName();
            ClassLoader unitClassLoader = SpringUnitUtil.getBundleClassLoader(springUnitBundle);
            // 单元默认配置类.
            final Class unitConfigurationClass = SpringUnitUtil.findDefaultConfigurationClass(springUnitBundle);//unitClassLoader.loadClass("org.union.sbp.springdemo.config.UnitConfiguration");
            Thread.currentThread().setContextClassLoader(unitClassLoader);
            ((AnnotationConfigServletWebServerApplicationContext)rootApplicationContext).setClassLoader(unitClassLoader);
            ((AnnotationConfigServletWebServerApplicationContext)rootApplicationContext).getBeanFactory().setBeanClassLoader(unitClassLoader);

            //先尝试关闭已有同名的context
            unitNamedFactory.closeContext(contextName);
            // 指定默认的配置类.
            UnitNamedSpec spec = new UnitNamedSpec(contextName, new Class[]{UnitBeanDefinitionRegistryPostProcessor.class, UnitWebConfig.class,unitConfigurationClass});
            List<UnitNamedSpec> specConfigurations = new ArrayList<UnitNamedSpec>();
            specConfigurations.add(spec);
            unitNamedFactory.setConfigurations(specConfigurations);

            AnnotationConfigApplicationContext unitApplicationContext = unitNamedFactory.getContextWithCreate(contextName);
            ResourceLoader resourceLoader = new UnitResourceLoader(unitClassLoader);
            unitApplicationContext.setResourceLoader(resourceLoader);
            unitApplicationContext.setClassLoader(unitClassLoader);
            unitApplicationContext.getBeanFactory().setBeanClassLoader(unitClassLoader);

            //暂时不需要这两行代码
            //SpringUnitApplication springUnitApplication = new SpringUnitApplication();
            //springUnitApplication.start(springUnitBundle,unitApplicationContext,new Object[]{unitClassLoader});

            //将子context中的swagger document对象加到主context中，以便统一管理.
            SwaggerAdaptor.addUnitSwagger(unitApplicationContext);
            //由于filter是全局的，所以需要加到主context中
            FilterAdaptor.addUnitFilter(unitApplicationContext);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }

        UnitLogger.logUserTimes(log,startTime,"完成springboot单元微内核适配");
    }
    /**
     * 单元卸载处理.
     * @param springUnitBundle
     */
    public static void stopSpringUnit(final Bundle springUnitBundle){
        AnnotationConfigApplicationContext applicationContext = unitNamedFactory.getContextByName(springUnitBundle.getSymbolicName());
        if(null == applicationContext){
            return;
        }
        try {
            FilterAdaptor.removeUnitFilter(applicationContext);
            SwaggerAdaptor.removeUnitSwagger(applicationContext);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        unitNamedFactory.closeContext(springUnitBundle.getSymbolicName());
        System.out.println("卸载spring单元");
    }
}
