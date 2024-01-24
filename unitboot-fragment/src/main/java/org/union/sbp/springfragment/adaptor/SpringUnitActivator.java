package org.union.sbp.springfragment.adaptor;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.union.sbp.springfragment.utils.SpringContextUtil;
import org.union.sbp.springfragment.utils.SpringStreamHanderFactoryUtil;
import org.union.sbp.springfragment.utils.SpringUnitUtil;
import org.union.sbp.springfragment.utils.UnitUtil;

/**
 * spring单元启动类父类，用于统一spring单元的启动与停止.
 *
 * @author youglu
 * @time 2024-01-24 11:39
 */
public abstract class SpringUnitActivator implements org.osgi.framework.BundleActivator {

    /**日志*/
    protected final Logger logger = LoggerFactory.getLogger(UnitApplicationContext.class);

    /**
     * 启动单元.
     *
     * @param context
     * @throws Exception
     */
    public void start(BundleContext context) throws Exception {
        SpringUnitUtil.initSpringBoot(context);
        ClassLoader currentThradClassLoader = Thread.currentThread().getContextClassLoader();
        // 设置当前线程的classloader为springboot启动类的classloader。
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {
            doStart(context);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }finally {
            Thread.currentThread().setContextClassLoader(currentThradClassLoader);
            // 重置回URLStreamHandlerFactory为equinox的版本.
            SpringStreamHanderFactoryUtil.resetSetOriginalUrlStreamHandlerFactory();
        }
        SpringUnitUtil.afterSpringContextInstance(context);
    }

    /**
     * 停止单元.
     * s
     * @param context
     * @throws Exception
     */
    public void stop(BundleContext context) throws Exception {
        doStop(context);
        UnitUtil.unRegistService(null);
    }

    /**
     * 子类需要实现的启动方法.
     *
     * @param context
     * @throws Exception
     */
    public abstract void doStart(BundleContext context)throws Exception;
    /**
     * 子类需要实现的停止s方法.
     *
     * @param context
     * @throws Exception
     */
    public abstract void doStop(BundleContext context)throws Exception;

}
