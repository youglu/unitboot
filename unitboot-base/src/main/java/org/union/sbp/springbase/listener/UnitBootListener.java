package org.union.sbp.springbase.listener;

import org.osgi.framework.BundleEvent;
import org.union.sbp.springbase.adaptor.SpringUnitBootAdaptor;

/**
 * 单元监听器，用于监听单元的启动与停止，以便初始化以及卸载spring单元.
 * @author youg
 * @since JDK1.8
 */
public class UnitBootListener implements org.osgi.framework.BundleListener {
    /**
     * 单元变更事件处理
     * @param event
     */
    @Override
    public synchronized void bundleChanged(BundleEvent event) {
        switch (event.getType()){
            case BundleEvent.STOPPED:
                SpringUnitBootAdaptor.stopSpringUnit(event.getBundle());
                break;
            case BundleEvent.STARTED:
                SpringUnitBootAdaptor.startSpringUnit(event.getBundle());
                break;
            default:
                break;
        }
    }
}
