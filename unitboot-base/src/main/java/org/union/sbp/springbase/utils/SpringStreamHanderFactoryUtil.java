package org.union.sbp.springbase.utils;


import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandlerFactory;

/**
 * 更换URLStreamHandlerFactory工具类.
 */
public class SpringStreamHanderFactoryUtil {

    private static URLStreamHandlerFactory originalUrlStreamHandlerFactory = null;

    /**
     * 清除URLStreamHandlerFactory，用于在springboot启动时会自动设置tomcat的URLStreamHandlerFactory。
     * @throws Exception
     */
    public static void clearUrlStreamHandlerFactory()
            throws Exception {
        final Field factoryField = URL.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        final Field lockField = URL.class.getDeclaredField("streamHandlerLock");
        lockField.setAccessible(true);
        synchronized (lockField.get(null)) {
            if(null == originalUrlStreamHandlerFactory) {
                originalUrlStreamHandlerFactory = (URLStreamHandlerFactory) factoryField.get(null);
            }
            factoryField.set(null, null);
        }
    }

    /**
     * 还原到equinox默认的URLStreamHandlerFactory，否则无法在控制台操作单元.
     * @throws Exception
     */
    public static void resetSetOriginalUrlStreamHandlerFactory()
            throws Exception {
        if(null == originalUrlStreamHandlerFactory){
            return;
        }
        final Class eclipseStarterClass = Class.forName("org.eclipse.core.runtime.adaptor.EclipseStarter");
        final Field frameworkField = eclipseStarterClass.getDeclaredField("framework");
        frameworkField.setAccessible(true);
        Object frameWork = frameworkField.get(null);

        final Field streamHandlerFactoryField = frameWork.getClass().getDeclaredField("streamHandlerFactory");
        streamHandlerFactoryField.setAccessible(true);
        originalUrlStreamHandlerFactory = (URLStreamHandlerFactory)streamHandlerFactoryField.get(frameWork);

        final Field factoryField = URL.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        final Field lockField = URL.class.getDeclaredField("streamHandlerLock");
        lockField.setAccessible(true);
        synchronized (lockField.get(null)) {
            factoryField.set(null, null);
            URL.setURLStreamHandlerFactory(originalUrlStreamHandlerFactory);
        }
    }
}
