package org.union.sbp.springbase.adaptor.config;

import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.osgi.framework.Bundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.union.sbp.springbase.adaptor.io.UnitResourceLoader;
import org.union.sbp.springbase.utils.SpringUnitUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 此类会优先其它类实例化并加载到spring bean factory中，
 * 用于参与bean的初始化，在此更新applicationContext的各种classloader。
 * @author youg
 * @since jdk1.8
 */
public class UnitBeanDefinitionRegistryPostProcessor /*extends ConfigurationClassPostProcessor*/ implements ApplicationContextAware {
    /**
     * 子context的环境变量
     */
    @Autowired
    private Environment environment;

    /**
     * 构造器
     */
    public UnitBeanDefinitionRegistryPostProcessor(){
        super();
    }

    /**
     * 重写设置环境变量方法.
     * @param environment
     */
    //@Override
    //public void setEnvironment(Environment environment) {
      //  super.setEnvironment(environment);
      //  this.environment = environment;
    //}

    /**
     * 重写设置ApplicationContext方法，此方法在setEnvironment之后调用.
     * @param applicationContext
     * @throws BeansException
     */
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AnnotationConfigApplicationContext unitApplicationContext = (AnnotationConfigApplicationContext)applicationContext;
        try {

            final Bundle unitBundle = SpringUnitUtil.getBundleByApplicationContext(unitApplicationContext);
            addUnitBootFlagProperty(applicationContext,unitBundle);

            ClassLoader unitClassLoader = ((BundleHost) unitBundle).getClassLoader();

            // unitApplicationContext.registerBean();

            ResourceLoader resourceLoader = new UnitResourceLoader(unitClassLoader);
            unitApplicationContext.setResourceLoader(resourceLoader);
            unitApplicationContext.setClassLoader(unitClassLoader);
            unitApplicationContext.getBeanFactory().setBeanClassLoader(unitClassLoader);

            /*
            StandardEnvironment standardEnvironment = (StandardEnvironment)unitApplicationContext.getParent().getEnvironment();

            String beanName = "org.springframework.boot.autoconfigure.internalCachingMetadataReaderFactory";
            unitApplicationContext.registerBean(beanName,UnitMetadataReaderFactory.class,null,null);
            unitApplicationContext.getBeanDefinition(beanName).setPrimary(true);

            Map<String, ConfigurationClassPostProcessor> beans = applicationContext.getBeansOfType(ConfigurationClassPostProcessor.class);
            beans.forEach((key,val)->{
                val.setResourceLoader(new UnitResourceLoader(applicationContext.getClassLoader()));
                val.setBeanClassLoader(unitClassLoader);
                val.setMetadataReaderFactory(new UnitMetadataReaderFactory());
            });
            */

            // 通过事件发起加载application.yml
            unitApplicationContext.register(ConfigFileApplicationListener.class);
            ConfigFileApplicationListener configFileApplicationListener = unitApplicationContext.getBean(ConfigFileApplicationListener.class);
            configFileApplicationListener.postProcessEnvironment(((AnnotationConfigApplicationContext) applicationContext).getEnvironment(),new SpringApplication());

            // 动态改变server.servlet.context-path
            if(null == environment){
                environment = unitApplicationContext.getEnvironment();
            }
            StandardEnvironment standardEnvironment = (StandardEnvironment)environment;
            final String unitName = unitBundle.getSymbolicName();
            String contextPath = standardEnvironment.getProperty("server.servlet.context-path","/");
            // 增加单元名称前缀，用于标识不同单元.
            contextPath = "/"+unitName+contextPath;
            MapPropertySource unitbootPropertySource = (MapPropertySource)standardEnvironment.getPropertySources().get("unitboot");//.addFirst("server.servlet.context-path",contextPath);
            unitbootPropertySource.getSource().put("server.servlet.context-path",contextPath);

            // 添加单元所有的包路径作为扫描bean的路径
            final Class activatorClass = SpringUnitUtil.getBundleActivatorClass(unitBundle);
            final Package classPackage = activatorClass.getPackage();
            unitApplicationContext.scan(classPackage.getName()+".**");
        } catch (Exception e) {
            throw new RuntimeException("添加子单元标识属性到子环境变量中发生异常",e);
        }
    }

    /**
     * 添加springboot单元特定的标识到环境变量.
     * @param unitApplicationContext
     * @param unitBundle 单元bundle
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void addUnitBootFlagProperty(ApplicationContext unitApplicationContext,final Bundle unitBundle) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(null == environment){
            environment = unitApplicationContext.getEnvironment();
        }
        final String unitName = unitBundle.getSymbolicName();
        StandardEnvironment standardEnvironment = (StandardEnvironment)environment;
        Map<String,Object> map = new HashMap<>();
        map.put("unitboot","true");
        map.put("unitName",unitName);
        PropertySource unitBootProperty = new MapPropertySource("unitboot",map);
        // 方式2，改变单元的server.servlet.context-path
        standardEnvironment.getPropertySources().addFirst(unitBootProperty);
    }
}
