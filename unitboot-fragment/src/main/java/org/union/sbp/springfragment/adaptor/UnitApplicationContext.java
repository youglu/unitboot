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

import javax.servlet.ServletContext;
import java.lang.reflect.Method;

public class UnitApplicationContext extends AnnotationConfigServletWebServerApplicationContext {

    @Override
    protected void onRefresh() { 
        // 获得WEB单元中提供的ServletContext
        final ServletContext servletContext = fetchServletContextFromOSGIService();
        if(null != servletContext){
            setServletContext(servletContext);
        }
        super.onRefresh();
    }

    private ServletContext fetchServletContextFromOSGIService(){
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        BundleContext bundleContext = bundle.getBundleContext();
        ServiceReference serviceReference = bundleContext.getServiceReference(ServletContext.class.getName());

        if(null != serviceReference){
           // ApplicationContextFacade servletContext = new ApplicationContextFacade(new ApplicationContext(new StandardContext()));
            //Object servletContextObj = bundleContext.getService(serviceReference);
            //BeanUtils.copyProperties(servletContextObj,applicationContextFacade);
            // 更改aplication的状态


            ServletContext servletContext = (ServletContext) bundleContext.getService(serviceReference);
            Method method = ReflectionUtils.findMethod(servletContext.getClass(),"setState");

            boolean isaccessible = method.isAccessible();
            method.setAccessible(true);
            ReflectionUtils.invokeMethod(method,new Object[]{LifecycleState.INITIALIZED});
            method.setAccessible(isaccessible);
            return servletContext;
        }
        return null;
    }

}