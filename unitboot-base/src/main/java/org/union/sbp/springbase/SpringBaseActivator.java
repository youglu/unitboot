package org.union.sbp.springbase;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.union.sbp.springbase.adaptor.SpringUnitBootAdaptor;
import org.union.sbp.springbase.listener.UnitBootListener;

/**
 * spring基础单元启动器.
 *
 * @author youg
 * @since JDK1.8
 */
public class SpringBaseActivator implements BundleActivator {

    /**
     * 启动.
     *
     * @param context BundleContext
     * @throws Exception 启动异常
     */
    public void start(BundleContext context) {
        SpringUnitBootAdaptor sdf = new SpringUnitBootAdaptor();
        System.out.println(org.eclipse.osgi.service.resolver.State.class);
        try {
            // 添加单元监听器，用于自动配置spring单元.
            context.addBundleListener(new UnitBootListener());
            System.out.println("spring基础环境初始化完毕");
        } catch (Exception e) {
            e.printStackTrace();
            // 停止springboot
			stopSpringBoot();
        }
    }

    /**
     * 停止.
     *
     * @param context BundleContext
     * @throws Exception 启动异常
     */
    public void stop(BundleContext context) throws Exception {
        //stopSpringBoot();
        //SpringStreamHanderFactoryUtil.resetSetOriginalUrlStreamHandlerFactory();
        System.out.println("停止spring基础单元");
    }

    /**
     * 初始化OSGI环境下的springboot
     *
     * @throws Exception 初始化异常
     */
    private void initSpringBoot() throws Exception {
        // 添加OSGI的FileLocator到Spring环境.
        //IoUtil.addOSGIScanPathResolve();
        //设置日志路径
        // IoUtil.getUnitUrl(context.getBundle(),"logback-spring.xml");
        // System.setProperty("logging.config", "classpath:logback-spring.xml");
        // 添除在equinox启动时，Framework设置的URLStreamHandlerFactory，因为springboot在嵌入tomcat时也要调置，但此属性设计只能设置一次，所以先清除。
       // SpringStreamHanderFactoryUtil.clearUrlStreamHandlerFactory();
        // 设置当前线程的classloader为springboot启动类的classloader。
       // Thread.currentThread().setContextClassLoader(SpringBootApplication.class.getClassLoader());
       // SpringBaseApplication.main(new String[]{});
        // 重置回URLStreamHandlerFactory为equinox的版本.
      //  SpringStreamHanderFactoryUtil.resetSetOriginalUrlStreamHandlerFactory();
    }

    /**
     * 停止springboot
     */
    private void stopSpringBoot() {

       /* final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) SpringContextUtil.getApplicationContext();
        if(null != applicationContext && applicationContext instanceof ServletWebServerApplicationContext) {
                WebServer webServer = ((ServletWebServerApplicationContext) applicationContext).getWebServer();
                if(null != webServer) {
                    ((ServletWebServerApplicationContext) applicationContext).getWebServer().stop();
                }
			}
			if (applicationContext.isActive()) {
				try {
					SpringApplication.exit(applicationContext);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}*/
		}

}
