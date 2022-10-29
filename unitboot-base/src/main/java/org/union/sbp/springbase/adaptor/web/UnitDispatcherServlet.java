package org.union.sbp.springbase.adaptor.web;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.union.sbp.springbase.adaptor.SpringUnitBootAdaptor;
import org.union.sbp.springbase.adaptor.UnitNamedFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 子context的DispatcherServlet,用于在更新单元时能够刷新DispatcherServlet.
 */
public class UnitDispatcherServlet extends DispatcherServlet {

    /**
     * 刷新标识，因为是从主context中的DispatcherServlet进行转发的，所以每次需要检查下是否子单元的有更新。
     */
    private boolean hasInit = false;

    /**
     * 获得是否刷新标识.
     * @return
     */
    public boolean isHasInit(){
        return hasInit;
    }

    /**
     * 带context参数刷新
     * @param context
     */
    public void refresh(ApplicationContext context) {
        if(!hasInit){
            hasInit = true;
            super.onRefresh(context);
        }
    }

    /**
     * 重写刷新
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        return super.getHandler(request);
    }
}
