package org.union.sbp.springbase.adaptor.web;

import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.union.sbp.springbase.adaptor.SpringUnitBootAdaptor;
import org.union.sbp.springbase.adaptor.UnitNamedFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 主context的DispatcherServlet子类，用于提供根据url是否要转到子单元的能力。
 * @author youg
 * @since JDK1.8
 */
public class RootDispatcherServlet extends DispatcherServlet {
    /**
     * 重写getHandler,用于尝试从url提取子单名名称进行转发。
     * @param request
     * @return
     * @throws Exception
     */
    @Nullable
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        // 根据URL的首位尝试获得子context
        String unitContextName = getUnitContextNameFromRequest(request);
        if(StringUtils.isEmpty(unitContextName)){
            return super.getHandler(request);
        }
        HandlerExecutionChain handlerExecutionChain = null;
        UnitDispatcherServlet unitDispatcherServlet = getUnitDispatcherServlet(request,unitContextName);
        if(null != unitDispatcherServlet) {
            handlerExecutionChain = unitDispatcherServlet.getHandler(new UnitRequestWrapper(request,unitContextName));
        }
        if(null == handlerExecutionChain){
            handlerExecutionChain = super.getHandler(request);
        }
        if(null == handlerExecutionChain || handlerExecutionChain.getHandler().toString().startsWith(BasicErrorController.class.getName())){
            System.out.println("无法获得handler："+request);
        }
        return handlerExecutionChain;
    }

    /**
     * 从请求上获得子单元名称，如果有的话.
     * @param request
     * @return String 子单元名称
     */
    private String getUnitContextNameFromRequest(final HttpServletRequest request) {
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
     * 根据单元名称获得子单元的DispatcherServlet bean。
     * @author youg
     * @param request
     * @param unitContextName 子单元名称
     * @return
     */
    private UnitDispatcherServlet getUnitDispatcherServlet(final HttpServletRequest request,final String unitContextName){
        if(StringUtils.isEmpty(unitContextName)){
           return null;
        }
        // 根据URL的首位尝试获得子context
        final UnitNamedFactory unitNamedFactory = (UnitNamedFactory) SpringUnitBootAdaptor.getUnitNamedFactory();
        final AnnotationConfigApplicationContext uniApplicationContext = unitNamedFactory.getContextByName(unitContextName);
        if(null != uniApplicationContext){
            final UnitDispatcherServlet unitDispatcherServlet = (UnitDispatcherServlet)unitNamedFactory.getInstance(unitContextName,DispatcherServlet.class);
            if(null != unitDispatcherServlet){
                if(!unitDispatcherServlet.isHasInit()){
                    unitDispatcherServlet.refresh(uniApplicationContext);
                }
                request.getServletContext();
                return unitDispatcherServlet;
            }
        }
        return null;
    }
}
