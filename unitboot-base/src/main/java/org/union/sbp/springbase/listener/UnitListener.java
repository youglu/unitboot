package org.union.sbp.springbase.listener;

import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.union.sbp.springbase.adaptor.SpringUnitBootAdaptor;
import org.union.sbp.springbase.utils.SpringUnitUtil;

public class UnitListener implements BundleListener {
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
