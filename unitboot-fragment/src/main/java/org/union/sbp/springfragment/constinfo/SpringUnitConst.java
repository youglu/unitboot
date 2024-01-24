package org.union.sbp.springfragment.constinfo;

/**
 * 常量接口.
 * @since JDK 1.8
 * @author youg
 */
public interface SpringUnitConst {
    /**
     * 单元启动器类名.
     */
    String UNIT_ACTIVATOR_CLASS_NAME = "Bundle-Activator";
    /**
     * 默认注册的dispatcherServlet名称
     */
    String DEFAULT_DISPATCHERSERVLET_NAME = "dispatcherServlet";

    /**web单元上下文配置项*/
    String CONTEXT_PATH = "server.servlet.context-path";
    String UNIT_DISPATCHERSERVLET_CHECKED = "checkUnitDispatcherServlet";
    String CHECKED = "1";
    String UNIT_SERVLETCONTEXT_APPLICATION_CONTEXT_NAME = "unitServletContextApplicationContext";
}
