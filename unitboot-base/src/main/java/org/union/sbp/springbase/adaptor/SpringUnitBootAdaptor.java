package org.union.sbp.springbase.adaptor;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;
import org.union.sbp.springbase.adaptor.config.SpringBaseConfiguration;
import org.union.sbp.springbase.adaptor.config.UnitBeanDefinitionRegistryPostProcessor;
import org.union.sbp.springbase.adaptor.config.UnitWebConfig;
import org.union.sbp.springbase.utils.SpringContextUtil;
import org.union.sbp.springbase.utils.SpringUnitUtil;
import org.union.sbp.springbase.utils.UnitLogger;
import unitlauncher.utils.SpringBootUnitThreadLocal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SpringBoot工程以OSGI单元形式运行.
 *
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
    private static final UnitNamedFactory unitNamedFactory = new UnitNamedFactory(SpringBaseConfiguration.class, "application.xml", "");
    /**
     * 保存各个子单元对应的specification配置,注意在停止时需要相应的清理.
     */
    // private static final List<UnitNamedSpec> specConfigurations = new ArrayList<UnitNamedSpec>();

    public static NamedContextFactory getUnitNamedFactory() {
        // 将子单元配置类集合加到namedFactory中,在创建子context时，会将对应的配置名称与上下名一致的类，以及default.开头的
        // 作为子上下文最先初始化的配置类进行加载.
        // unitNamedFactory.setConfigurations(specConfigurations);
        return unitNamedFactory;
    }

    /**
     * 单元安装处理.
     *
     * @param springUnit
     */
    public synchronized static void startSpringUnit(final Bundle springUnit) {

        final long startTime = System.currentTimeMillis();
        final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // SpringBootUnitThreadLocal.set(springUnit.getBundleId());
            //如果不是spring单元测不处理
            String unitType = springUnit.getHeaders().get("Unit-Type");
            if (StringUtils.isEmpty(unitType) || !SRING_UNIT.equals(unitType)) {
                UnitLogger.info(log, "当前单元不是Spring单元，不进行Spring适配");
                return;
            }
            // 如果是当前单元则跳过.
            if (springUnit.getSymbolicName().equals(FrameworkUtil.getBundle(SpringUnitUtil.class).getSymbolicName())) {
                return;
            }
            //设置主springContext,如果没有则设置
            if(! unitNamedFactory.isHasParentContext()) {
                ApplicationContext rootApplicationContext = SpringContextUtil.getApplicationContext();
                unitNamedFactory.setApplicationContext(rootApplicationContext);
            }
            // 创建单元子上下文
            final String contextName = getContextName(springUnit);
            ClassLoader unitClassLoader = SpringUnitUtil.getBundleClassLoader(springUnit);

            Thread.currentThread().setContextClassLoader(unitClassLoader);
            //AnnotationConfigServletWebServerApplicationContext RootConfigServletWebServerApplicationContext = (AnnotationConfigServletWebServerApplicationContext)rootApplicationContext;
            //RootConfigServletWebServerApplicationContext.setClassLoader(unitClassLoader);
            //RootConfigServletWebServerApplicationContext.getBeanFactory().setBeanClassLoader(unitClassLoader);

            //先尝试关闭已有同名的context
            // unitNamedFactory.closeContext(contextName);

            // 单元默认配置类.
            final List<Class> unitConfigurationClassList = SpringUnitUtil.findDefaultConfigurationClass(springUnit);
            // 指定默认的配置类.
            unitConfigurationClassList.add(0, UnitBeanDefinitionRegistryPostProcessor.class);
            unitConfigurationClassList.add(1, UnitWebConfig.class);

            UnitNamedSpec spec = new UnitNamedSpec(contextName, unitConfigurationClassList.toArray(new Class<?>[unitConfigurationClassList.size()]));
            List<UnitNamedSpec> specConfigurations = new ArrayList<>();
            specConfigurations.add(spec);
            unitNamedFactory.setConfigurations(specConfigurations);

            AnnotationConfigApplicationContext unitApplicationContext = unitNamedFactory.getContextWithCreate(contextName);
            //ResourceLoader resourceLoader = new UnitResourceLoader(unitClassLoader);
            //unitApplicationContext.setResourceLoader(resourceLoader);
            //unitApplicationContext.setClassLoader(unitClassLoader);
            //unitApplicationContext.getBeanFactory().setBeanClassLoader(unitClassLoader);

            //暂时不需要这两行代码
            //SpringUnitApplication springUnitApplication = new SpringUnitApplication();
            //springUnitApplication.start(springUnit,unitApplicationContext,new Object[]{unitClassLoader});

            //将子context中的swagger document对象加到主context中，以便统一管理.
            SwaggerAdaptor.addUnitSwagger(unitApplicationContext);
            //由于filter是全局的，所以需要加到主context中
            FilterAdaptor.addUnitFilter(unitApplicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            // SpringBootUnitThreadLocal.remove();
        }
        UnitLogger.logUserTimes(log, startTime, "完成springboot单元微内核适配");

    }

    /**
     * 单元卸载处理.
     *
     * @param springUnit
     */
    public synchronized static void stopSpringUnit(final Bundle springUnit) {
        final String contextName = getContextName(springUnit);
        final AnnotationConfigApplicationContext applicationContext = unitNamedFactory.getContextByName(contextName);
        if (null != applicationContext) {
            try {
                FilterAdaptor.removeUnitFilter(applicationContext);
                SwaggerAdaptor.removeUnitSwagger(applicationContext);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        unitNamedFactory.closeContext(contextName);
        // final Iterator<UnitNamedSpec> iterator = specConfigurations.iterator();
        // while (iterator.hasNext()) {
        //    if (iterator.next().getName().equals(contextName)) {
        //        iterator.remove();
        //        break;
        //    }
        // }
        UnitLogger.info(log,"卸载spring单元:{}",contextName);
    }

    /**
     * 根据单元对象获得spring上下文名称.
     *
     * @param springUnit spring单元对象.
     * @return String
     */
    public static String getContextName(final Bundle springUnit) {
        return springUnit.getBundleId() + "|" + springUnit.getSymbolicName();
    }
}
