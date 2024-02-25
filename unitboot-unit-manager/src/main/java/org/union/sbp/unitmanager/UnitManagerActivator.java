package org.union.sbp.unitmanager;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.union.sbp.springfragment.adaptor.SpringUnitActivator;
import org.union.sbp.springfragment.adaptor.SpringUnitApplication;

public class UnitManagerActivator extends SpringUnitActivator {

	private ServiceRegistration ctxserviceref = null;

	public void doStart(BundleContext context) throws Exception {
		SpringUnitApplication.run(new String[]{});
		System.out.println("unit manager初始化完毕");
	}

	public void doStop(BundleContext context) throws Exception {
		System.out.println("停止unit manager单元");
	}
}
