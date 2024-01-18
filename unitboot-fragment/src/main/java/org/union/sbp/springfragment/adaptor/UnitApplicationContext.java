package org.union.sbp.springfragment.adaptor;

import com.fasterxml.jackson.databind.util.BeanUtil;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.catalina.core.StandardContext;
import org.eclipse.osgi.framework.internal.core.BundleContextImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.union.sbp.springfragment.utils.ReflectUtil;

import javax.servlet.ServletContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UnitApplicationContext extends AnnotationConfigServletWebServerApplicationContext {

    @Override
    protected void onRefresh() { 
        // 获得WEB单元中提供的ServletContext
        final ServletContext servletContext = null;//fetchServletContextFromOSGIService();
        if(null != servletContext){
            final int STARTING_PREP = 3;
            changeWebContextState(servletContext, STARTING_PREP);
            setServletContext(servletContext);
        }

        super.onRefresh();

        if(null != servletContext) {
            final int STARTED = 5;
            changeWebContextState(servletContext,STARTED);
        }

    }

    private ServletContext fetchServletContextFromOSGIService(){
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        BundleContext bundleContext = bundle.getBundleContext();
        ServiceReference serviceReference = bundleContext.getServiceReference(ServletContext.class.getName());

        if(null != serviceReference){
            ServletContext servletContext = (ServletContext) bundleContext.getService(serviceReference);
            return servletContext;
        }
        return null;
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