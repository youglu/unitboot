package org.union.sbp.springweb;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringWebActivator implements BundleActivator {

	private  AnnotationConfigApplicationContext ctx;
	private ServiceRegistration ctxserviceref = null;

	public void start(BundleContext context) throws Exception {
		SprinpWebApplication.main(new String[]{});
		System.out.println("spring web初始化完毕");
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("停止spring web单元");
	}
}
