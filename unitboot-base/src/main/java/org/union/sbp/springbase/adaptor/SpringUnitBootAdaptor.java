package org.union.sbp.springbase.adaptor;

import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.eclipse.osgi.framework.internal.core.BundleFragment;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.eclipse.osgi.framework.internal.core.Framework;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * SpringBoot工程以OSGI单元形式运行.
 *
 * @author youg
 * @since jdk1.8
 */
public class SpringUnitBootAdaptor {

    private final static Logger log = LoggerFactory.getLogger(SpringUnitBootAdaptor.class);
    /**
     * 单元安装处理.
     *
     * @param springUnit
     */
    public synchronized static void startSpringUnit(final Bundle springUnit) {
    }

    /**
     * 单元卸载处理.
     *
     * @param springUnit
     */
    public synchronized static void stopSpringUnit(final Bundle springUnit) {

    }

    /**
     * 根据单元对象获得spring上下文名称.
     *
     * @param springUnit spring单元对象.
     * @return String
     */
    public static String getContextName(final Bundle springUnit) {
        return springUnit.getBundleId() + "|" + springUnit.getSymbolicName();
    }

    /**
     * 让fragment支持多host
     *
     * @author youg
     * @param startingBundle 正在启动的单元
     */
    public static void attachFragment(final Bundle startingBundle) {
        // 尝试附加当前处理单元的fragment，因为在update时单元的fragment已置为空,这样在加载fragment中的类时就无法找到了.

        if (null != startingBundle && startingBundle instanceof BundleHost) {
            final BundleHost bundleHost = (BundleHost) startingBundle;
            final BundleFragment[] fragments = bundleHost.getFragments();
            if (null == fragments || fragments.length <= 0) {
                final Framework framework = bundleHost.getFramework();
                final BundleDescription[] bundleDescriptions = framework.getAdaptor().getState().getBundles();
                for (BundleDescription bundleDescription : bundleDescriptions) {
                    final AbstractBundle abstractBundle = (AbstractBundle) bundleDescription.getBundle();
                    if (abstractBundle instanceof BundleFragment) {
                        String hostBundleId = abstractBundle.getHeaders().get("Fragment-Host");
                        for(String bundleId:hostBundleId.split(",")){
                            if (bundleId.equals(startingBundle.getSymbolicName())) {
                                try {
                                    bundleHost.attachFragment((BundleFragment) abstractBundle);
                                } catch (BundleException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
