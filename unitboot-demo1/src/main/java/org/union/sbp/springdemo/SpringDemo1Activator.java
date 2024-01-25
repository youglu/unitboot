package org.union.sbp.springdemo;

import org.osgi.framework.BundleContext;
import org.union.sbp.springfragment.adaptor.SpringUnitActivator;
import org.union.sbp.springfragment.adaptor.SpringUnitApplication;
import org.union.sbp.springfragment.utils.IoUtil2;
import org.union.sbp.springfragment.utils.SpringStreamHanderFactoryUtil;

public class SpringDemo1Activator extends SpringUnitActivator {

    public void doStart(BundleContext context) throws Exception {
        SpringUnitApplication.run(new String[]{});
        System.out.println("spring demo初始化完毕");
    }

    public void doStop(BundleContext context) throws Exception {
        System.out.println("停止spring demo单元");
    }


}
