package unitlauncher.adaptor;

import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.bundlefile.ZipBundleEntry;
import org.eclipse.osgi.baseadaptor.bundlefile.ZipBundleFile;
import org.eclipse.osgi.baseadaptor.hooks.ClassLoadingStatsHook;
import org.eclipse.osgi.baseadaptor.loader.ClasspathEntry;
import org.eclipse.osgi.baseadaptor.loader.ClasspathManager;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegateHook;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.eclipse.osgi.internal.loader.BundleLoader;
import org.eclipse.osgi.internal.loader.BundleLoaderProxy;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unitlauncher.UnionAdaptor;
import unitlauncher.utils.GlobalClassLoader;
import unitlauncher.utils.GlobalResourceLoader;
import unitlauncher.utils.ReflectUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Enumeration;

/**
 * 处理单元A持有单元B加载类时，然而单元B更新后导致的文件不存在异常处理(虽然不影响功能).
 *
 * @date 2023-11-28
 * @author youg
 * @since JDK1.8
 */
public class UnitClasspathLoaderClassHook implements ClassLoadingStatsHook {
    /**
     * 日志.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    public UnitClasspathLoaderClassHook() {

    }

    @Override
    public void preFindLocalClass(String s, ClasspathManager classpathManager) throws ClassNotFoundException {
        // 检查当前类加载器对应的单元是否是旧的，如果是旧的，则对应的entries中的classpathEntry状态为close
        // 如果是则清空当前单元的classpathmanager中所有的entries.
        final ClasspathEntry[] entries = classpathManager.getHostClasspathEntries();
        if(null == entries || entries.length <= 0){
            return;
        }
        try {
            // 检查是否资源已关闭，相应的单元也是旧的了
            for(int i=0,L=entries.length;i<L;i++){
                ClasspathEntry entry = entries[i];
                if(null != entry && entry.getBundleFile() instanceof ZipBundleFile){
                    final ZipBundleFile zipBundleFile = (ZipBundleFile) entry.getBundleFile();
                    final File zipFile = zipBundleFile.getBaseFile();
                    if(!zipFile.exists()){
                        entries[i] = null;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public void postFindLocalClass(String s, Class<?> aClass, ClasspathManager classpathManager) throws ClassNotFoundException {

    }

    @Override
    public void preFindLocalResource(String s, ClasspathManager classpathManager) {

    }

    @Override
    public void postFindLocalResource(String s, URL url, ClasspathManager classpathManager) {

    }

    @Override
    public void recordClassDefine(String s, Class<?> aClass, byte[] bytes, ClasspathEntry classpathEntry, BundleEntry bundleEntry, ClasspathManager classpathManager) {

    }
}