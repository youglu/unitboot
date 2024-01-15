package org.union.sbp.springdemo;

import org.osgi.framework.BundleContext;
import org.union.sbp.springfragment.utils.IoUtil2;
import org.union.sbp.springfragment.utils.SpringContextUtil;
import org.union.sbp.springfragment.utils.SpringStreamHanderFactoryUtil;

public class SpringDemo1Activator implements org.osgi.framework.BundleActivator {

    public void start(BundleContext context) throws Exception {
        initSpringBoot(context);
        System.out.println("spring demo初始化完毕");
    }

    public void stop(BundleContext context) throws Exception {
        System.out.println("停止spring demo单元");
    }

    /**
     * 初始化OSGI环境下的springboot
     *
     * @throws Exception 初始化异常
     */
    private void initSpringBoot(final BundleContext context) throws Exception {
        // 添加OSGI的FileLocator到Spring环境.
        IoUtil2.addOSGIScanPathResolve();
        //设置日志路径
        IoUtil2.getUnitUrl(context.getBundle(), "logback-spring.xml");
        System.setProperty("logging.config", "classpath:logback-spring.xml");
        // 添除在equinox启动时，Framework设置的URLStreamHandlerFactory，因为springboot在嵌入tomcat时也要调置，但此属性设计只能设置一次，所以先清除。
        SpringStreamHanderFactoryUtil.clearUrlStreamHandlerFactory();
        ClassLoader currentThradClassLoader = Thread.currentThread().getContextClassLoader();
        // 设置当前线程的classloader为springboot启动类的classloader。
        Thread.currentThread().setContextClassLoader(SpringDemoApplication.class.getClassLoader());
        SpringDemoOsgiApplication.run(new String[]{});
        Thread.currentThread().setContextClassLoader(currentThradClassLoader);
        // 重置回URLStreamHandlerFactory为equinox的版本.
        SpringStreamHanderFactoryUtil.resetSetOriginalUrlStreamHandlerFactory();
    }
}
