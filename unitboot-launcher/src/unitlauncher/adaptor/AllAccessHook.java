package unitlauncher.adaptor;

import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegateHook;
import unitlauncher.utils.GlobalClassLoader;
import unitlauncher.utils.GlobalResourceLoader;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Enumeration;

/**
 * 自定义默认采用JVM类加载器加载指名类名的类
 * @author youg
 * @since JDK1.8
 */
public class AllAccessHook implements ClassLoaderDelegateHook {

    private final GlobalClassLoader globalClassLoader;
    private final GlobalResourceLoader globalResourceLoader;
    private BaseAdaptor adaptor = null;
    private String[] proxyClassNames = new String[]{"com.zaxxer.hikari.HikariDataSource"};
    private String[] proxyResourceNames = new String[]{"org/mybatis/spring/boot/autoconfigure","com/alibaba/druid/spring/boot/autoconfigure"};

    public AllAccessHook(BaseAdaptor adaptor) {
        this.adaptor = adaptor;
        System.out.println("初始化AllAccessHook,adaptor="+adaptor);
        globalClassLoader = GlobalClassLoader.getInstance(adaptor);
        globalResourceLoader = GlobalResourceLoader.getInstance(adaptor);
    }

    /**
     * 前置类加载.
     * @param className
     * @param bundleClassLoader
     * @param bundleData
     * @return
     * @throws ClassNotFoundException
     */
        @Override
        public Class<?> preFindClass(String className, BundleClassLoader bundleClassLoader, BundleData bundleData) throws ClassNotFoundException {
           return null;
        }

    /**
     * 按单元的查找类流程处理完后找不到会再调用此方法.
     * 在此方法后还有两个查找：1、PolicyHandler，2：父加载器.
     * @param className
     * @param bundleClassLoader
     * @param bundleData
     * @return
     * @throws ClassNotFoundException
     */
        @Override
        public Class<?> postFindClass(String className, BundleClassLoader bundleClassLoader, BundleData bundleData) throws ClassNotFoundException {
            if(null == className){
                return null;
            }
            boolean canProxy = false;
            for(String clazzName:proxyClassNames){
                if(clazzName.equals(className)){
                    canProxy = true;
                    break;
                }
            }
            if(!canProxy){
                return null;
            }
            // 尝试全局加载
            Class<?> clazz = globalClassLoader.findLocalClass(className,bundleData.getBundle());
            // System.out.println("尝试全局加载加载类-->"+className+"="+clazz);
            return clazz;
        }

        @Override
        public URL preFindResource(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
            return null;
        }

        @Override
        public URL postFindResource(String resourceName, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
            boolean canProxy = false;
            for(String proxyResourceName:proxyResourceNames){
                if(resourceName.startsWith(proxyResourceName)){
                    canProxy = true;
                    break;
                }
            }
            if(!canProxy){
                return null;
            }
            System.out.println("全局查询资源:"+resourceName);
            return globalResourceLoader.findResource(resourceName,bundleData.getBundle());
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