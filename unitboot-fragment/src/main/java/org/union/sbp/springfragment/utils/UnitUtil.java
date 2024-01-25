package org.union.sbp.springfragment.utils;

import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单元工具类，用于提供方便获得单元的各种方法.
 */
public class UnitUtil {

    private static final Logger logger = LoggerFactory.getLogger(UnitUtil.class);
    /**
     * 已注册的spring实例到osgi的服务引用,停止时需要解除注册
     */
    private static final Map<String,ServiceRegistration> registedServiceRegistrations = new HashMap<>();
    /**
     * 根据单元标识符获得单元.
     * @param synblicName 单元标识符
     * @return bundle
     */
    public static Bundle getBundleBySynblicName(final String synblicName){
       // UnitLogger.info(LOGGER,"根据单元标识符:{}获得单元",synblicName);
        Bundle bundle = FrameworkUtil.getBundle(UnitUtil.class);
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
        return getBundle(unitId);
    }
    /**
     * 获得单元，如果有指定单ID，则按此ID获得.
     * @param unitId 单元id
     * @return bundle
     */
    public static Bundle getBundle(final Long unitId){
        //UnitLogger.info(LOGGER,"根据单元id:{}获得单元",unitId);
        Bundle bundle = FrameworkUtil.getBundle(SpringUnitUtil.class);
        if(null == unitId) {
            return bundle.getBundleContext().getBundle();
        }else{
            return bundle.getBundleContext().getBundle(unitId);
        }
    }
    /**
     * 根据服务类名获得相应的服务数组.
     * @param serviceName 服务名
     * @return bundle
     */
    public static <T> List<T> getUnitServiceListByServiceName(final String serviceName){
        Bundle bundle = FrameworkUtil.getBundle(SpringUnitUtil.class);
        try {
            final BundleContext bundleContext = bundle.getBundleContext();
            final ServiceReference<T>[] serviceReferences = (ServiceReference<T>[]) bundleContext.getServiceReferences(serviceName, null);
            if(null != serviceReferences && serviceReferences.length > 0){
                List<T>  serviceList = new ArrayList<T>(serviceReferences.length);
                for(ServiceReference<T> serviceReference:serviceReferences){
                    serviceList.add(bundleContext.getService(serviceReference));
                }
                return serviceList;
            }
        } catch (InvalidSyntaxException e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * 注册服务到osgi环境
     *
     * @param service 注册的服务实例
     * @param serviceName 注册的服务名
     * @param <T> 服务类型
     */
    public static <T> void registService(T service,String serviceName){
        final Bundle bundle = FrameworkUtil.getBundle(SpringUnitUtil.class);
        final BundleContext bundleContext = bundle.getBundleContext();
        // 尝试先解除
        unRegistService(serviceName);
        registedServiceRegistrations.put(serviceName,bundleContext.registerService(serviceName, service, null));
    }

    /**
     * 解除注册到osgi环境的spring相关bean服务.此方法需要在停止时调用.
     *
     * @param serviceName 指定要解除的服务名
     * @author youg
     */
    public static void unRegistService(final String serviceName){
        if (registedServiceRegistrations.isEmpty()) {
            return;
        }

        final Bundle bundle = FrameworkUtil.getBundle(UnitUtil.class);
        if (bundle == null) {
            logger.error("Unable to get the bundle for OSGiServiceUtil");
            return;
        }

        final BundleContext bundleContext = bundle.getBundleContext();
        if (bundleContext == null) {
            logger.error("Unable to get the bundle context for the bundle");
            return;
        }

        registedServiceRegistrations.entrySet().removeIf(entry -> {
            boolean shouldUnregister = StringUtils.isEmpty(serviceName) || serviceName.equals(entry.getKey());
            if (shouldUnregister) {
                try {
                    entry.getValue().unregister();
                } catch (IllegalStateException exception) {
                    logger.error(exception.getMessage(), exception);
                }
            }
            return shouldUnregister;
        });
    }

    /**
     * 根据类获得相应的单元
     * @param clax
     * @return
     */
    public static Bundle getBundleByClass(Class clax) {
        return FrameworkUtil.getBundle(clax);
    }
}
