package org.union.sbp.springfragment.adaptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.union.sbp.springfragment.utils.ReflectUtil;
import org.union.sbp.springfragment.utils.SpringUnitUtil;

import javax.servlet.ServletContext;

@SpringBootApplication()
public class UnitApplicationContext extends AnnotationConfigServletWebServerApplicationContext {


    protected final Logger logger = LoggerFactory.getLogger(UnitApplicationContext.class);

    @Override
    protected void onRefresh() { 
        // 获得WEB单元中提供的ServletContext
        final ServletContext servletContext = SpringUnitUtil.fetchServletContextFromOSGIService();
        if(null != servletContext){
            final int STARTING_PREP = 3;
            changeWebContextState(servletContext, STARTING_PREP);
            setServletContext(servletContext);
            // 定自定义一个属性保存applicationContext实例，在多单元环境下dispatcherServlet初始化时getWebApplicationContext:127, WebApplicationContextUtils会用于
        }
        super.onRefresh();

        if(null != servletContext) {
            final int STARTED = 5;
            changeWebContextState(servletContext,STARTED);
        }
    }



    /**
     * 更改tomcat容器状态,参考 org.apache.catalina.LifecycleState类中的枚举位置.
     *
     * @param servletContext
     * @param stateIndex
     */
    private void changeWebContextState(final ServletContext servletContext, int stateIndex){
        try {
            Object standardContext = ReflectUtil.getFieldValue("context",servletContext);
            Object webServerContext = ReflectUtil.getFieldValue("context",standardContext);
            if(null != webServerContext) {
                Class lifecycleEnumClass = webServerContext.getClass().getClassLoader().loadClass("org.apache.catalina.LifecycleState");
                Object[] lifecycleEnums = lifecycleEnumClass.getEnumConstants();
                Object startedLifecycleState = lifecycleEnums[stateIndex];
                ReflectUtil.setFieldValue("state",webServerContext,startedLifecycleState);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}