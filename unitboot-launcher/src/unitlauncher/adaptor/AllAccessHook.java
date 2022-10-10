package unitlauncher.adaptor;

import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegateHook;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Enumeration;

/**
 * 自定义默认采用JVM类加载器加载指名类名的类
 * @author youg
 * @since JDK1.8
 */
public class AllAccessHook implements ClassLoaderDelegateHook {

    /**
     * 前置类加载.
     * @param s
     * @param bundleClassLoader
     * @param bundleData
     * @return
     * @throws ClassNotFoundException
     */
        @Override
        public Class<?> preFindClass(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) throws ClassNotFoundException {
            try{
                // 尝试用JVM默认的类加载器加载
               return Class.forName(s);
            }catch(Exception e){
                // ignore exception
            }
            return null;
        }

        @Override
        public Class<?> postFindClass(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) throws ClassNotFoundException {
            return null;
        }

        @Override
        public URL preFindResource(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
            return null;
        }

        @Override
        public URL postFindResource(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
            return null;
        }

        @Override
        public Enumeration<URL> preFindResources(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
            return null;
        }

        @Override
        public Enumeration<URL> postFindResources(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
            return null;
        }

        @Override
        public String preFindLibrary(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
            return null;
        }

        @Override
        public String postFindLibrary(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) {
            return null;
        }
    }