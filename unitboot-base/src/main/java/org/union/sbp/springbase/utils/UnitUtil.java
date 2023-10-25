package org.union.sbp.springbase.utils;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.union.sbp.springbase.SpringBaseActivator;

/**
 * 单元工具类，用于提供方便获得单元的各种方法.
 */
public class UnitUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitUtil.class);
    /**
     * 根据单元标识符获得单元.
     * @param synblicName 单元标识符
     * @return bundle
     */
    public static Bundle getBundleBySynblicName(final String synblicName){
        UnitLogger.info(LOGGER,"根据单元标识符:{}获得单元",synblicName);
        Bundle bundle = FrameworkUtil.getBundle(SpringBaseActivator.class);
        Bundle[] bundles = bundle.getBundleContext().getBundles();
        for(Bundle unit:bundles){
            if(unit.getSymbolicName().equals(synblicName)){
                return unit;
            }
        }
        return null;
    }

    /**
     * 根据单元id获得单元.
     * @param unitId 单元id
     * @return bundle
     */
    public static Bundle getBundleByBundleId(final long unitId){
        UnitLogger.info(LOGGER,"根据单元id:{}获得单元",unitId);
        Bundle bundle = FrameworkUtil.getBundle(SpringBaseActivator.class);
        return bundle.getBundleContext().getBundle(unitId);
    }
}
