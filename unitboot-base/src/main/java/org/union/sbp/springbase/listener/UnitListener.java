package org.union.sbp.springbase.listener;

import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.union.sbp.springbase.adaptor.SpringUnitBootAdaptor;
import org.union.sbp.springbase.utils.SpringUnitUtil;

/**
 * 单元监听器，用于监听单元的启动与停止，以便初始化以及卸载spring单元.
 * @author youg
 * @since JDK1.8
 */
public class UnitListener implements BundleListener {
    /**
     * 单元变更事件处理
     * @param event
     */
    @Override
    public void bundleChanged(BundleEvent event) {
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
