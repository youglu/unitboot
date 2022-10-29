package org.union.sbp.springdemo;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringDemo1Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		System.out.println("spring demo初始化完毕");
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("停止spring demo单元");
	}
}
