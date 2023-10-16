package com.unitor.sbp.iotsearch;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class HymdActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		System.out.println("Hymd应用初始化完毕");
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("停止Hymd应用单元");
	}
}
