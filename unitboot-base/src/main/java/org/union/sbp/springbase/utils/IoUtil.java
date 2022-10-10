package org.union.sbp.springbase.utils;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

/**
 * osgi资源转常规则文件URL工具类.
 * @author youg
 * @since JDK1.8
 */
public class IoUtil {
    /**
     * 从osgi单元获得资源，转换为为file协议.
     * @param unit
     * @param resourcePath
     * @return
     */
    public static URL getUnitUrl(final Bundle unit,final String resourcePath){
        try {
            final URL resource = unit.getResource(resourcePath);
            Bundle equinoxCommonBundle = getEclipseCommonBundle();
            if(null == equinoxCommonBundle){
                return resource;
            }
            final Class FileLocatorClaass = equinoxCommonBundle.loadClass("org.eclipse.core.runtime.FileLocator");
            final Method resolveMethod = FileLocatorClaass.getMethod("resolve", java.net.URL.class);
            final java.net.URL resolvedUrl = (java.net.URL)resolveMethod.invoke(null,resource);
            return resolvedUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 为spring的类扫描路径增加osgi资资解析处理。（将获得的:bundleresource://xxx/xxx.xx转换为绝对路径file:///xxx/xxx.xx）.
     * spring的类PathMatchingResourcePatternResolver有静态块会尝试检查是否是OSGI环境，如是则会设置Equinox的资源处理。
     */
    public static void addOSGIScanPathResolve() {
        try {
            final Field equinoxResolveMethodField = PathMatchingResourcePatternResolver.class.getDeclaredField("equinoxResolveMethod");
            boolean fieldAccessAble = equinoxResolveMethodField.isAccessible();
            equinoxResolveMethodField.setAccessible(true);

            final Bundle equinoxCommonBundle = SpringUnitUtil.getBundleBySymbolicName("org.eclipse.equinox.common", "3.6.200.v20130402-1505");
            final Class FileLocatorClaass = equinoxCommonBundle.loadClass("org.eclipse.core.runtime.FileLocator");
            final Method resolveMethod = FileLocatorClaass.getMethod("resolve", URL.class);
            equinoxResolveMethodField.set(null,resolveMethod);
            equinoxResolveMethodField.setAccessible(fieldAccessAble);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 固定获得eclipse common单元
     * @return Bundle
     */
    private static Bundle getEclipseCommonBundle() {
        return SpringUnitUtil.getBundleBySymbolicName("org.eclipse.equinox.common", "3.6.200.v20130402-1505");
    }
}
