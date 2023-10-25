package org.union.sbp.springbase.utils;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.union.sbp.springbase.adaptor.annoatation.UnitConfigComponent;
import org.union.sbp.springbase.adaptor.io.UnitResourceLoader;
import org.union.sbp.springbase.constinfo.SpringUnit;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * spring单元处理工具类
 * @author youg
 * @since JDK1.8
 */
public class SpringUnitUtil {

    /**
     * 获得Spring单元默认的配置类名,完整的类名.
     * @param bundle
     * @return
     */
    public static List<Class> findDefaultConfigurationClass(final Bundle bundle){
        try {
            //保存单元初始配置类
            final List<Class> unitConfigClassList = new ArrayList<Class>();
            //尝试从单元的classpath获得注解为UnitConfiguration的类
            final Class activatorClass = getBundleActivatorClass(bundle);
            final ClassPathScanningCandidateComponentProvider scanner=new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(UnitConfigComponent.class));
            scanner.setResourceLoader(new UnitResourceLoader(activatorClass.getClassLoader()));
            final Set<BeanDefinition> unitConfigClasseDefinitions = scanner.findCandidateComponents(activatorClass.getPackage().getName()+"");
            if(null != unitConfigClasseDefinitions && unitConfigClasseDefinitions.size()>0){
                unitConfigClasseDefinitions.forEach((beanDefinition)->{
                    try {
                        unitConfigClassList.add(bundle.loadClass(beanDefinition.getBeanClassName()));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
            //如果没有找到，则尝试从单元元文件获得配置类
            final String defaultSpringConfigurationClassName = bundle.getHeaders().get("Spring-ConfigurationClass");
            if(unitConfigClasseDefinitions.isEmpty() && !StringUtils.isEmpty(defaultSpringConfigurationClassName)) {
                String[] configClassNames = defaultSpringConfigurationClassName.split(",");
                for(String configClassName:configClassNames) {
                    unitConfigClassList.add(bundle.loadClass(configClassName));
                }
            }
            return unitConfigClassList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 根据单元标识获得单元实例.
     * @return Bundle
     */
    public static Bundle getBundleBySymbolicName(final String synbolicName,final String version){
        try {
            final Class eclipseStarter = Class.forName("org.eclipse.core.runtime.adaptor.EclipseStarter");
            final Field frameworkField = eclipseStarter.getDeclaredField("framework");
            frameworkField.setAccessible(true);
            final Object framework = frameworkField.get(null);
            final Method getBundleBySymbolicName = framework.getClass().getDeclaredMethod("getBundleBySymbolicName",String.class, Version.class);

            final List<Object> bundleList = (List<Object>) getBundleBySymbolicName.invoke(framework, new Object[]{synbolicName, Version.parseVersion(version)});
            if (null == bundleList || bundleList.isEmpty()) {
                return null;
            }
            return (Bundle) bundleList.get(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得单元的类加载器，用于加载本单元中的类.
     * @param unitBundle
     * @return
     */
    public static ClassLoader getBundleClassLoader(final Bundle unitBundle){
        String activatorClassName = unitBundle.getHeaders().get(SpringUnit.UNIT_ACTIVATOR_CLASS_NAME);
        try {
           return unitBundle.loadClass(activatorClassName).getClassLoader();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据单元获得单元的启动器类
     * @param unitBundle
     * @param <T>
     * @return
     */
    public static <T> Class<T> getBundleActivatorClass(final Bundle unitBundle){
        String activatorClassName = unitBundle.getHeaders().get(SpringUnit.UNIT_ACTIVATOR_CLASS_NAME);
        try {
            if(StringUtils.isEmpty(activatorClassName)){
                return null;
            }
            return (Class<T>) unitBundle.loadClass(activatorClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 根据子context获得相应的bundle,主要是解析applicatonName来获得.由于在统一处理时，有用
     * 单元的 id|synblicName 人为context的名称，因此这里可以获得相应的内容.
     * @param unitApplicationContext
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Bundle getBundleByApplicationContext(final ApplicationContext unitApplicationContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(null == unitApplicationContext){
            return null;
        }
        final String contextName = unitApplicationContext.getDisplayName();
        if(contextName.indexOf("|") != -1){
            String[] unitId_synblicName = contextName.split("\\|");
            String unitId = unitId_synblicName[0].split("\\-")[1];
            return UnitUtil.getBundleByBundleId(Long.valueOf(unitId));
        }
        ClassLoader unitClassLoader = unitApplicationContext.getClassLoader();
        if(!unitClassLoader.getClass().getName().equals("org.eclipse.osgi.internal.baseadaptor.DefaultClassLoader")){
            return null;
        }
        Method getBundle = unitClassLoader.getClass().getDeclaredMethod("getBundle");
        Bundle unitBundle = (Bundle) getBundle.invoke(unitClassLoader);
        if(null == unitBundle){
            throw new RuntimeException("无法从子单元ApplicationContext获得有效的单元");
        }
        return unitBundle;
    }
}
