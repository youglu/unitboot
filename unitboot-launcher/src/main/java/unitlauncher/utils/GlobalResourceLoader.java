package unitlauncher.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * 全局类加载器，用于从所有单元中加载指定类名的类.
 * @author youg
 * @since JDK1.8
 */
public class GlobalResourceLoader {
    private final static Log log = LogFactory.getLog(GlobalResourceLoader.class);
    private BaseAdaptor adaptor;
    private static GlobalResourceLoader globalResourceLoader = null;

    private GlobalResourceLoader(){

    }
    public synchronized static GlobalResourceLoader getInstance(final BaseAdaptor adaptor){
        if(null  == globalResourceLoader){
            globalResourceLoader = new GlobalResourceLoader();
            globalResourceLoader.adaptor = adaptor;
        }
        return globalResourceLoader;
    }

    public URL findResource(final String className, final Bundle excludeBundle)  {
        BundleContext context =  adaptor.getContext();
        if(null == context){
            return null;
        }
        final Bundle[] allbundles = context.getBundles();
        List<Bundle> bundleList = Arrays.asList(allbundles);
        log.info("将从" + bundleList.size() + "个单元中找资源:" + className);
        return singleTheadFind(className,bundleList,excludeBundle);
    }

    private URL singleTheadFind(final String resourceName, final List<Bundle> bundleList, final Bundle excludeBundle){
        URL findClass = null;
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
                //BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
                BundleClassLoader classLoader = (BundleClassLoader)bundleHost.getClassLoader();
                findClass = classLoader.findLocalResource(resourceName);
                if (null != findClass) {
                    log.info("从第" + bundle + "找到"+resourceName);
                    return findClass;
                }
            } catch (Throwable e) {
                //ignore exception
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