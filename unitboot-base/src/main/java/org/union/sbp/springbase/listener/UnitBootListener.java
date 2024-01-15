package org.union.sbp.springbase.listener;

import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.eclipse.osgi.framework.internal.core.BundleFragment;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.eclipse.osgi.framework.internal.core.Framework;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.SynchronousBundleListener;
import org.union.sbp.springbase.adaptor.SpringUnitBootAdaptor;

/**
 * 单元监听器，用于监听单元的启动与停止，以便初始化以及卸载spring单元.
 *
 * @author youg
 * @since JDK1.8
 */
public class UnitBootListener implements SynchronousBundleListener {
    /**
     * 单元变更事件处理
     *
     * @param event
     */
    @Override
    public synchronized void bundleChanged(BundleEvent event) {
        System.out.print("事件类型----->:" + event.getType());
        switch (event.getType()) {
            case BundleEvent.STARTING:
                System.out.println(":开始启动中...");
                // 在调用activator前附加fragment单元,以便使用增强的classpath
                SpringUnitBootAdaptor.attachFragment(event.getBundle());
                break;
            case BundleEvent.STOPPED:
                System.out.println(":已停止");
                //SpringUnitBootAdaptor.stopSpringUnit(event.getBundle());
                break;
            case BundleEvent.STARTED:
                System.out.println(":启动完毕");
                //SpringUnitBootAdaptor.startSpringUnit(event.getBundle());
                break;
            case BundleEvent.UPDATED:
                System.out.println(":更新完毕");
                break;
            default:
                System.out.println("");
                break;
        }
    }
}
