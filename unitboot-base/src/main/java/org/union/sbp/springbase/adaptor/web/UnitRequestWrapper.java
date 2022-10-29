package org.union.sbp.springbase.adaptor.web;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 适用于单元的HttpServletRequest包装类.
 * @author youg
 * @since JDK1.8
 */
public class UnitRequestWrapper extends HttpServletRequestWrapper {
    /**
     * 单元名称
     */
    private String unitName;
    public UnitRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    public UnitRequestWrapper(HttpServletRequest request,final String unitName) {
        this(request);
        this.unitName = unitName;
        if(!StringUtils.isEmpty(unitName) && !unitName.startsWith("/")){
            this.unitName = "/"+this.unitName;
        }
    }
    @Override
    public String getContextPath() {
        if(StringUtils.isEmpty(unitName)){
           return super.getContextPath();
        }
        return unitName;
    }
    @Override
    public String getServletPath() {
        String servletPath = super.getServletPath();
        if(!StringUtils.isEmpty(unitName) && servletPath.startsWith(getContextPath())){
            servletPath = servletPath.substring(unitName.length());
        }
        return servletPath;
    }
}
