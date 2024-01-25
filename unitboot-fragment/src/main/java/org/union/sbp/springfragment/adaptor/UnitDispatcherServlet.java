package org.union.sbp.springfragment.adaptor;

import org.osgi.framework.Bundle;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.union.sbp.springfragment.constinfo.SpringUnitConst;
import org.union.sbp.springfragment.utils.SpringContextUtil;
import org.union.sbp.springfragment.utils.SpringUnitUtil;
import org.union.sbp.springfragment.utils.UnitUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 主context的DispatcherServlet子类，用于提供根据url是否要转到子单元的能力。
 * @author youg
 * @since JDK1.8
 */
public class UnitDispatcherServlet extends DispatcherServlet {

    @Override
    public String getContextAttribute() {
        return UnitUtil.getBundle(null).getSymbolicName()+"."+ SpringUnitConst.UNIT_SERVLETCONTEXT_APPLICATION_CONTEXT_NAME;
    }
    @Override
    public String getServletContextAttributeName() {
        this.getServletContext();
        return getContextAttribute();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        initInfo();
        // 检查是否已经检测过了
        if(request.getAttribute(SpringUnitConst.UNIT_DISPATCHERSERVLET_CHECKED) != null){
            super.service(request, response);
            return;
        }
        // 根据请求路径获得相应单元的dispatcherServlet实例
        final HttpServlet unitDispatcherServlet = getUnitServletDispatcher(request);
        request.setAttribute(SpringUnitConst.UNIT_DISPATCHERSERVLET_CHECKED, SpringUnitConst.CHECKED);
        if(null == unitDispatcherServlet){
            super.service(request, response);
        }else {
            unitDispatcherServlet.service(request,response);
        }
    }

    /**
     * 从请求URL的前缀匹配各单元的上下文，如匹配到，则使用该单元处理请求。
     * @param request
     * @return
     * @throws Exception
     */
    @Nullable
    protected HttpServlet getUnitServletDispatcher(HttpServletRequest request)  {
        final List<HttpServlet> dispatcherServletServiceList = UnitUtil.getUnitServiceListByServiceName(DispatcherServlet.class.getName());
        if(null == dispatcherServletServiceList || dispatcherServletServiceList.size() == 1){
            return null;
        }else{
            for(HttpServlet dispatcherServlet:dispatcherServletServiceList){
                // 获得当前dispatcherServlet所在单元的标识名
                final String unitSymbolicName = getUnitSymbolicNameFromRequest(request);
                final Bundle unit = UnitUtil.getBundleByClass(dispatcherServlet.getClass());
                if(null != unit && unit.getSymbolicName().equals(unitSymbolicName)){
                    return dispatcherServlet;
                }
            }
        }
        return null;
    }/**
     * 从请求上获得子单元名称，如果有的话.
     * @param request
     * @return String 子单元名称
     */
    private String getUnitSymbolicNameFromRequest(final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        //取路径的第一节,/a/b->/a
        if("/".equals(requestUri) || requestUri.length()<=1){
            return null;
        }
        String[] pathItems = requestUri.substring(1).split("/");
        if(pathItems.length < 2){
            return null;
        }
        return pathItems[0];
    }

    /**
     * 多单元环境下初始化.
     */
    private void initInfo(){
        // 初始化
        if(null != SpringUnitUtil.fetchServletContextFromOSGIService() && (null == this.getServletConfig() || (null != this.getServletConfig() && null == this.getServletContext()))) {
            final UnitServletConfig unitServletConfig = new UnitServletConfig(this.getServletName());
            final ServletContext servletContext = unitServletConfig.getServletContext();
            // 初始化时会执行 WebApplicationContextUtils.getWebApplicationContext方法，在osgi环境会报错，处理之.
            final Object appcationContext = servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            try {
                // 临时清除，使初始化时采用自定义的contextAttribute属性获得
                servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, null);
                // 将当前单元的applicationContext实例设置到自定义属性对应的值中
                servletContext.setAttribute(this.getContextAttribute(), SpringContextUtil.getApplicationContext());
                this.init(unitServletConfig);
            } catch (ServletException e) {
                logger.error(e.getMessage(), e);
            }finally {
                // 还原默认的，因为servletContext是web单元统一创建的唯一实例
                servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appcationContext);
            }
        }
    }
}
