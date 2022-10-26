package unitlauncher.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 全局类加载器，用于从所有单元中加载指定类名的类.
 * @author youg
 * @since JDK1.8
 */
public class GlobalClassLoader {
    private final static Log log = LogFactory.getLog(GlobalClassLoader.class);
    private BaseAdaptor adaptor;

    private static GlobalClassLoader globalClassLoader = null;
    private GlobalClassLoader(){

    }
    public synchronized static GlobalClassLoader getInstance(final BaseAdaptor adaptor){
        if(null  == globalClassLoader){
            globalClassLoader = new GlobalClassLoader();
            globalClassLoader.adaptor = adaptor;
        }
        return globalClassLoader;
    }


    public Class<?> findLocalClass(final String className,final Bundle excludeBundle)  {
        BundleContext context = adaptor.getContext();
        if(null == context){
            return null;
        }
        final Bundle[] allbundles = context.getBundles();
        List<Bundle> bundleList = Arrays.asList(allbundles);
        log.info("将从" + bundleList.size() + "个单元中找类:" + className);
        return singleTheadFind(className,bundleList,excludeBundle);
    }
    private Class singleTheadFind(final String className, final List<Bundle> bundleList, final Bundle excludeBundle){
        Class findClass = null;
        for(Bundle bundle:bundleList) {
            if(!checkBundleCanBeFind(bundle,excludeBundle)){
                continue;
            }
            if(!(bundle instanceof BundleHost)){
                continue;
            }
            try {
                BundleHost bundleHost = (BundleHost)bundle;
                if(!(bundleHost.getClassLoader() instanceof BundleClassLoader)){
                    continue;
                }
                BundleClassLoader classLoader = (BundleClassLoader)bundleHost.getClassLoader();
                findClass = classLoader.findLocalClass(className);
                if (null != findClass) {
                    // 检查类的classloader是否与单元一致，在单元更新后，没有及时回收，导致存在多个，所以这里判断下.
                    BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
                    ClassLoader checkClassLoader = bundleWiring.getClassLoader();
                    if (checkClassLoader != findClass.getClassLoader()) {
                       continue;
                    }
                    log.info("从第" + bundle + "找到类");
                    return findClass;
                }
            } catch (Throwable e) {
                //ignore exception
            } finally {

            }
        }
        return null;
    }

    private boolean checkBundleCanBeFind(final Bundle bundle,final Bundle excludeBundle){
        if(Bundle.ACTIVE != bundle.getState()){
            return false;
        }
        // 由于0号单元的classloader不是Classloader的类型，因此调用bundleWiring.getClassLoader()里面会强转，从而报异常，因此这里判断跳过0号单元.
        if(bundle.getBundleId() == 0){
            return false;
        }
        // 如果是要排除的单元，则跳过
        if(null != excludeBundle && excludeBundle.getBundleId() == bundle.getBundleId()){
            return false;
        }
        return true;
    }

}