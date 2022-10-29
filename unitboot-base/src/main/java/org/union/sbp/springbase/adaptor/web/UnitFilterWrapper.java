package org.union.sbp.springbase.adaptor.web;

import javax.servlet.*;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * 单元过滤器包装类，用于增加单元名称，在删除时可以根据单元名称解除与删除filter。
 * @author youg
 * @since JDK1.8
 */
public class UnitFilterWrapper implements Filter {
    /**
     * 所属单元名称.
     */
    private String unitName;
    /**
     * 实际代理的filter。
     */
    private Filter proxyFilter;

    /**
     * 过滤器
     * @param proxyFilter
     * @param unitName
     */
    public UnitFilterWrapper(Filter proxyFilter,@NotNull  final String unitName){
        this.proxyFilter = proxyFilter;
        this.unitName = unitName;
    }

    /**
     * 重写初始化
     * @param filterConfig
     * @throws ServletException
     */
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
}
