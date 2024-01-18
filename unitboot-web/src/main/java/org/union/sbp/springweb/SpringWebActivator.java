package org.union.sbp.springweb;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.union.sbp.springfragment.utils.SpringUnitUtil;
import org.union.sbp.springweb.config.UnitConfiguration;
import org.union.sbp.springweb.utils.SpringStreamHanderFactoryUtil;

import javax.servlet.ServletContext;

public class SpringWebActivator implements BundleActivator {

	private ServiceRegistration<?> serviceRegistration;

	public void start(BundleContext context) {
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			SpringUnitUtil.initSpringBoot(context);
			SpringStreamHanderFactoryUtil.clearUrlStreamHandlerFactory();
			SprinpWebApplication.main(new String[]{});
			// 重置回URLStreamHandlerFactory为equinox的版本.
			SpringStreamHanderFactoryUtil.resetSetOriginalUrlStreamHandlerFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**注册webcontext服务到当前进程*/
		ServletContext servletContext = UnitConfiguration.getApplicationContext().getBean(ServletContext.class);
		serviceRegistration = context.registerService(ServletContext.class.getName(),servletContext,null);

		System.out.println("spring web初始化完毕");
	}

	public void stop(BundleContext context) {
		if(null != serviceRegistration) {
			serviceRegistration.unregister();
			serviceRegistration = null;
		}
		stopSpringBoot();
		System.out.println("停止spring web单元");
	}

	/**
	 * 停止springboot
	 */
	private void stopSpringBoot() {

       final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) UnitConfiguration.getApplicationContext();
        if(null != applicationContext ) {
        	if(applicationContext instanceof ServletWebServerApplicationContext){
				WebServer webServer = ((ServletWebServerApplicationContext) applicationContext).getWebServer();
				if (null != webServer) {
					webServer.stop();
				}
			}
			if (applicationContext.isActive()) {
				try {
					SpringApplication.exit(applicationContext);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
