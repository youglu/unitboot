package org.union.sbp.springbase.adaptor.web;

import javax.servlet.*;
import javax.validation.constraints.NotNull;
import java.io.IOException;

public class UnitFilterWrapper implements Filter {
    /**
     * 所属单元名称.
     */
    private String unitName;
    private Filter proxyFilter;
    public UnitFilterWrapper(Filter proxyFilter,@NotNull  final String unitName){
        this.proxyFilter = proxyFilter;
        this.unitName = unitName;
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        proxyFilter.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        proxyFilter.doFilter(request,response,chain);
    }

    @Override
    public void destroy() {
        proxyFilter.destroy();
    }
    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
