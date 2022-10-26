package org.union.sbp.springbase.adaptor;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.union.sbp.springbase.adaptor.web.UnitFilterRegistrationBean;
import org.union.sbp.springbase.adaptor.web.UnitFilterWrapper;
import org.union.sbp.springbase.utils.SpringContextUtil;
import org.union.sbp.springbase.utils.SpringContollerUtil;
import org.union.sbp.springbase.utils.SpringUnitUtil;
import org.union.sbp.springbase.utils.UnitLogger;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * spring子单元controller适配器.
 * @author youg
 * @since jdk1.8
 */
public class FilterAdaptor {
    private final static Logger log = LoggerFactory.getLogger(FilterAdaptor.class);
    /**
     * swagger controller开头的包前缀，子单元的swagger的controller不需要加到主context中。
     */
    private final static String SWAGGER_CONTROLLER_PACKAGE_PREFIX = "springfox.documentation.swagger";
    /**
     * 注册子context的conoller bean到主context。
     * @param unitApplicationContext
     * @throws Exception
     */
    public static void addUnitFilter(final AnnotationConfigApplicationContext unitApplicationContext) throws Exception {
        final long startTime = System.currentTimeMillis();
        final Map<String, Filter> filterMap = unitApplicationContext.getBeansOfType(Filter.class);
        if (null == filterMap || filterMap.isEmpty()) {
            return;
        }
        final StandardContext standardContext = getStandardContextFromRootApplicationContext();
        final ServletContext servletContext = SpringContextUtil.getServletContext();

        //设置tomcat状态为预启动
        final Field stateField = ReflectionUtils.findField(StandardContext.class,"state");
        stateField.setAccessible(true);
        final LifecycleState contextState = (LifecycleState)ReflectionUtils.getField(stateField,standardContext);
        ReflectionUtils.setField(stateField,standardContext,LifecycleState.STARTING_PREP);

        // 根据子context获得相应的单元.
        final Bundle unitBundle = SpringUnitUtil.getBundleByApplicationContext(unitApplicationContext);
        final String unitName = unitBundle.getSymbolicName();
        for(Map.Entry<String,Filter> entry:filterMap.entrySet()){
            final Filter filter = entry.getValue();
            final UnitFilterRegistrationBean filterRegistrationBean = new UnitFilterRegistrationBean(unitName,new UnitFilterWrapper(filter,unitName));
            WebFilter webFilter = AnnotationUtils.findAnnotation(filter.getClass(), WebFilter.class);
            if(null != webFilter){
                filterRegistrationBean.setName(webFilter.filterName());
                filterRegistrationBean.setUrlPatterns(Arrays.asList(webFilter.urlPatterns()));
            }
            filterRegistrationBean.onStartup(servletContext);
        }
        standardContext.filterStart();
        // 还原tomcat状态
        ReflectionUtils.setField(stateField,standardContext,contextState);
        UnitLogger.logUserTimes(log,startTime,"完成filter适配");
    }
    /**
     * 从主context解除子context注册的所有filter.
     * @param unitApplicationContext
     * @throws Exception
     */
    public static void removeUnitFilter(final AnnotationConfigApplicationContext unitApplicationContext){
        final Map<String, Filter> filterBeanMap = unitApplicationContext.getBeansOfType(Filter.class);
        if (null == filterBeanMap || filterBeanMap.isEmpty()) {
            return;
        }
        StandardContext standardContext = getStandardContextFromRootApplicationContext();
        // 这里会存在并发问题，如果锁住，在线运行会暂停.
        for(Map.Entry<String,Filter> entry:filterBeanMap.entrySet()){
            String filterName = entry.getValue().getClass().getSimpleName();
            WebFilter webFilter = AnnotationUtils.findAnnotation(entry.getValue().getClass(), WebFilter.class);
            if(null != webFilter){
                filterName = webFilter.filterName();
            }
            FilterDef filterDef = standardContext.findFilterDef(filterName);
            // FilterConfig filterConfig = standardContext.findFilterConfig(filterName);
            FilterMap[] filterMaps = standardContext.findFilterMaps();
            //移除FilterDef
            standardContext.removeFilterDef(filterDef);
            //移除FilterMap
            List<FilterMap> needRemoveFilterMapList = new ArrayList<FilterMap>();
            for(FilterMap filterMap:filterMaps){
                if(filterMap.getFilterName().equals(filterName)) {
                    needRemoveFilterMapList.add(filterMap);
                }
            }
            needRemoveFilterMapList.forEach(filterMap -> {
                standardContext.removeFilterMap(filterMap);
            });
            needRemoveFilterMapList.clear();
            needRemoveFilterMapList = null;
        }
        standardContext.filterStart();
        // 停止单元后需要将子单元的swagger信息从主单元中移除.
        SwaggerAdaptor.removeUnitSwagger(unitApplicationContext);
    }

    /**
     * 从主context中获得StandardContext
     * @return
     */
    private static StandardContext getStandardContextFromRootApplicationContext(){
        ServletContext servletContext = SpringContextUtil.getServletContext();
        // 更改context状态
        ApplicationContextFacade applicationContextFacade = ((ApplicationContextFacade)servletContext);
        Field appContextField = ReflectionUtils.findField(ApplicationContextFacade.class,"context");
        appContextField.setAccessible(true);

        ApplicationContext applicationContext = (ApplicationContext) ReflectionUtils.getField(appContextField,applicationContextFacade);
        Field contextField = ReflectionUtils.findField(ApplicationContext.class,"context");
        contextField.setAccessible(true);
        StandardContext standardContext = (StandardContext)ReflectionUtils.getField(contextField,applicationContext);
        return standardContext;
    }
}
