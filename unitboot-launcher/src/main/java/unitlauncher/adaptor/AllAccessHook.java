package unitlauncher.adaptor;

import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegateHook;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.eclipse.osgi.internal.loader.BundleLoader;
import org.eclipse.osgi.internal.loader.BundleLoaderProxy;
import org.eclipse.osgi.service.resolver.BundleDescription;
import unitlauncher.utils.GlobalClassLoader;
import unitlauncher.utils.GlobalResourceLoader;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
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
    private String[] proxyClassNames = new String[]{
            "com.zaxxer.hikari.HikariDataSource",
            "tk.mybatis.mapper.autoconfigure",
            "org.h2.Driver",
            "org.h2.upgrade.DbUpgrade"};
    private String[] proxyResourceNames = new String[]{
            "org/mybatis/spring/boot/autoconfigure",
            "com/alibaba/druid/spring/boot/autoconfigure",
            "tk/mybatis/mapper/autoconfigure",
    "db/schema.sql",
    "db/data.sql"};


    public AllAccessHook(BaseAdaptor adaptor) {
        this.adaptor = adaptor;
       // System.out.println("初始化AllAccessHook,adaptor="+adaptor);
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
           // System.out.println("preFindClass:"+className);
           // resolveBundleMappingDifBundle(bundleClassLoader);
            // 部分包走单元自已的加载器
            if(className.equals("javax.servlet.GenericFilter")
                   // || className.equals("javax.servlet.http.PushBuilder")
                    //|| className.equals("javax.servlet.http.MappingMatch")

            ){
                return null;
            }else
                // 公共包走全局局加载
            if(className.equals("javax.servlet.ServletContext")
                    || className.equals("javax.servlet.Servlet")
                    || className.equals("javax.servlet.ServletConfig")
                    || className.equals("javax.servlet.ServletRequest")
                    || className.equals("javax.servlet.ServletResponse")
                    || className.equals("javax.servlet.Filter")
                    || className.equals(FilterConfig.class.getName())
                    || className.equals(FilterChain.class.getName())
                    || className.equals(ServletRegistration.class.getName())
                    || className.equals(ServletRegistration.Dynamic.class.getName())
                    || className.equals(Registration.class.getName())
                    || className.equals(Registration.Dynamic.class.getName())
                    || className.equals("javax.servlet.Registration$Dynamic")
                    || className.equals("javax.servlet.FilterRegistration$Dynamic")
                    || className.equals(FilterRegistration.class.getName())
                    || className.equals(DispatcherType.class.getName())
                    || className.equals(RequestDispatcher.class.getName())
                    || className.startsWith("javax.servlet")
                ){
                return this.getClass().getClassLoader().loadClass(className);
            }
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

            return null;
        }

        @Override
        public URL preFindResource(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
           // System.out.println("preFindResource:"+s);
            return null;
        }

        @Override
        public URL postFindResource(String resourceName, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
          return null;
        }

        @Override
        public Enumeration<URL> preFindResources(String resourceName, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
           // System.out.println("preFindResources:"+resourceName);
            return null;
        }

        @Override
        public Enumeration<URL> postFindResources(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
           // System.out.println("postFindResources"+s);
            return null;
        }

        @Override
        public String preFindLibrary(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) throws FileNotFoundException {
           // System.out.println("preFindLibrary:"+s);
            return null;
        }

        @Override
        public String postFindLibrary(String s, BundleClassLoader bundleClassLoader, BundleData bundleData) {
           // System.out.println("postFindLibrary"+s);
            return null;
        }

    /**
     * 修复在查找类时，ResolverImpl保存的单元与当前运行不一致问题.
     * 原因是BundleClassLoader的代理BundleLoader中的proxy中的BundleDescription没有更新到为当前bundle的BundleDescription
     * @param bundleClassLoader
     */
    private void resolveBundleMappingDifBundle(BundleClassLoader bundleClassLoader){
        try {
            BundleLoader bundleLoader = (BundleLoader) bundleClassLoader.getDelegate();
            BundleLoaderProxy proxy =  bundleLoader.getLoaderProxy();
            BundleDescription proxyBundleDescription = proxy.getBundleDescription();
            BundleDescription currentBundleDescription = ((BundleHost) bundleClassLoader.getBundle()).getBundleDescription();
            if(proxyBundleDescription != currentBundleDescription){
                Field bundleDescriptionField = BundleLoaderProxy.class.getDeclaredField("description");
                Boolean accessState = bundleDescriptionField.isAccessible();
                bundleDescriptionField.setAccessible(true);
                bundleDescriptionField.set(proxy,currentBundleDescription);
                bundleDescriptionField.setAccessible(accessState);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}