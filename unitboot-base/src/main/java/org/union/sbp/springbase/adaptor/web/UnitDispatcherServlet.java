package org.union.sbp.springbase.adaptor.web;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.union.sbp.springbase.adaptor.SpringUnitBootAdaptor;
import org.union.sbp.springbase.adaptor.UnitNamedFactory;

import javax.servlet.http.HttpServletRequest;

public class UnitDispatcherServlet extends DispatcherServlet {

    private boolean hasInit = false;
    public boolean isHasInit(){
        return hasInit;
    }
    public void setHasInit(boolean hasInit){
        this.hasInit = hasInit;
    }

    public void refresh(ApplicationContext context) {
        if(!hasInit){
            hasInit = true;
            super.onRefresh(context);
        }
    }
    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        return super.getHandler(request);
    }
}
