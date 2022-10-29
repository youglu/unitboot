package org.union.sbp.springbase.adaptor.io;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.union.sbp.springbase.utils.IoUtil;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URL;

/**
 * 单元资源加载器
 *
 * @author youg
 * @since JDK1.8
 */
public class UnitResourceLoader extends ClassPathXmlApplicationContext implements ResourcePatternResolver {
    /**
     * 指定的资源加载的类加载器.
     */
    private ClassLoader classLoader;

    /**
     * 构造器.
     * @param classLoader
     */
    public UnitResourceLoader(final ClassLoader classLoader) {
        IoUtil.addOSGIScanPathResolve();
        if(null != classLoader) {
            this.classLoader = classLoader;
        }
    }

    /**
     * 加载资源,返回资源数组.
     * @param s
     * @return
     * @throws IOException
     */
    @Override
    public Resource[] getResources(String s) throws IOException {
        System.out.println(s);
        return super.getResources(s);
    }
    /**
     * 加载资源.
     * @param location
     * @return
     * @throws IOException
     */
    @Override
    public Resource getResource(String location) {
        System.out.println(location);
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new UnitResourceWrapper(location.substring(CLASSPATH_URL_PREFIX.length()), classLoader);
        }else {
            return super.getResource(location);
        }
    }
    /**
     * 获得加载资源的classloader。
     * @return
     */
    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;//Thread.currentThread().getContextClassLoader();
    }
}