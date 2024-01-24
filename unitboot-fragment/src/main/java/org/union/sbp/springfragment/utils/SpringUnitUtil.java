package org.union.sbp.springfragment.utils;

import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.union.sbp.springfragment.constinfo.SpringUnitConst;

import javax.servlet.ServletContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * spring单元处理工具类
 * @author youg
 * @since JDK1.8
 */
public class SpringUnitUtil {

    /**日志*/
    protected final static Logger logger = LoggerFactory.getLogger(SpringUnitUtil.class);
    /**
     * 获得Spring单元默认的配置类名,完整的类名.
     * @param bundle
     * @return
     */
    public static List<Class> findDefaultConfigurationClass(final Bundle bundle){


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
        String activatorClassName = unitBundle.getHeaders().get(SpringUnitConst.UNIT_ACTIVATOR_CLASS_NAME);
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
        String activatorClassName = unitBundle.getHeaders().get(SpringUnitConst.UNIT_ACTIVATOR_CLASS_NAME);
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
    /**
     * 初始化OSGI环境下的springboot
     *
     * @throws Exception 初始化异常
     */
    public static void initSpringBoot(final BundleContext context) throws Exception {
        // 添加OSGI的FileLocator到Spring环境.
        IoUtil2.addOSGIScanPathResolve();
        //设置日志路径
        IoUtil2.getUnitUrl(context.getBundle(), "logback-spring.xml");
        System.setProperty("logging.config", "classpath:logback-spring.xml");
        // 添除在equinox启动时，Framework设置的URLStreamHandlerFactory，因为springboot在嵌入tomcat时也要调置，但此属性设计只能设置一次，所以先清除。
        SpringStreamHanderFactoryUtil.clearUrlStreamHandlerFactory();
    }
    /**
     * 根据当前的class类获得对应的单元，从中提取dispatcherServlet的注册名称.
     *
     * @return String
     */
    public static String getDispatcherServletRegistName() {
        Bundle bundle = FrameworkUtil.getBundle(SpringUnitUtil.class);
        if(null != bundle){
            String synblicName_unitId = bundle.getSymbolicName()+"|"+bundle.getBundleId();
            return synblicName_unitId;
        }
        return SpringUnitConst.DEFAULT_DISPATCHERSERVLET_NAME;
    }
    /**
     * 根据当前的class类获得对应的单元，从中提取dispatcherServlet的注册名称.
     *
     * @return String
     */
    public static String getUnitContextPath() {
        return getUnitContextPath(SpringUnitUtil.class);
    }
    /**
     * 根据当前的class类获得对应的单元，从中提取dispatcherServlet的注册名称.
     *
     * @param clasz 从该class的所属以cloassloader获得单元实例
     * @return String
     */
    public static String getUnitContextPath(final Class clasz) {
        final Bundle unit = FrameworkUtil.getBundle(clasz);
        if(null != unit){
            String springContext = System.getProperty(unit.getBundleId()+"."+ SpringUnitConst.CONTEXT_PATH);
            return springContext;
        }
        return "/";
    }

    /**
     * 获得注册共享的servletContext实例
     * @return ServletContext
     */
    public static ServletContext fetchServletContextFromOSGIService(){
        final Bundle bundle = FrameworkUtil.getBundle(SpringUnitConst.class);
        final BundleContext bundleContext = bundle.getBundleContext();
        final ServiceReference serviceReference = bundleContext.getServiceReference(ServletContext.class.getName());

        if(null != serviceReference){
            ServletContext servletContext = (ServletContext) bundleContext.getService(serviceReference);
            return servletContext;
        }

        return null;
    }

    /**
     * spring单元在spring applicationContext实例化后调用。
     *
     * @param context
     */
    public static void afterSpringContextInstance(BundleContext context) {
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        if(null == applicationContext){
            logger.warn("spring applicationContext实例为空，无法注册到osgi环境，这将导致相关功能不可用！要");
            return;
        }
        // 设置web上下文到单元的配置中
        Bundle unit = context.getBundle();
        System.setProperty(unit.getBundleId()+"."+ SpringUnitConst.CONTEXT_PATH, applicationContext.getEnvironment().getProperty(SpringUnitConst.CONTEXT_PATH));
        UnitUtil.registService(applicationContext, ApplicationContext.class.getName());
    }
}
