package org.union.sbp.springdemo2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringDemoActivator implements BundleActivator {

	private  AnnotationConfigApplicationContext ctx;
	private ServiceRegistration ctxserviceref = null;

	public void start(BundleContext context) throws Exception {
		System.out.println("spring demo2初始化完毕");
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("停止spring demo2单元");
	}
}
