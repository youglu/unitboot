package org.union.sbp.springbase.utils;

import org.eclipse.osgi.internal.loader.BundleLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.context.ApplicationContext;
import org.union.sbp.springbase.constinfo.SpringUnit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * spring单元处理工具类
 */
public class SpringUnitUtil {

    /**
     * 获得Spring单元默认的配置类名,完整的类名.
     * @param bundle
     * @return
     */
    public static Class findDefaultConfigurationClass(final Bundle bundle){
        try {
            final String defaultSpringConfigurationClassName = bundle.getHeaders().get("Spring-ConfigurationClass");
            return bundle.loadClass(defaultSpringConfigurationClassName);

            //Method getClassLoaderMethod = ReflectUtils.findDeclaredMethod(bundle.getClass(),"getClassLoader",new Class[]{});
            //getClassLoaderMethod.setAccessible(true);
            //Object bundleLoaderObj = getClassLoaderMethod.invoke(bundle,null);
            // ClassLoader bundleLoader = ((BundleHost) bundle).getClassLoader();
            //if(null != bundleLoaderObj){
             //   BundleLoader bundleLoader = (BundleLoader)bundleLoaderObj;
            //}
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
    public static Bundle getBundleByApplicationContext(final ApplicationContext unitApplicationContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(null == unitApplicationContext){
            return null;
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
