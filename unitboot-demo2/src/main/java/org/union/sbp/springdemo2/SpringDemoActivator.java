package org.union.sbp.springdemo2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.union.sbp.springbase.SpringBaseActivator;
import org.union.sbp.springfragment.adaptor.SpringUnitActivator;
import org.union.sbp.springfragment.adaptor.SpringUnitApplication;

public class SpringDemoActivator extends SpringUnitActivator {

	private ServiceRegistration ctxserviceref = null;

	public void doStart(BundleContext context) throws Exception {
		SpringUnitApplication.run(new String[]{});
		System.out.println("spring demo2初始化完毕");
	}

	public void doStop(BundleContext context) throws Exception {
		System.out.println("停止spring demo2单元");
	}
}
