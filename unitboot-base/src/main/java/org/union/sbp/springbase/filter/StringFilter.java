package org.union.sbp.springbase.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;

/**
 * 测试filter
 * @author youg
 * @since JDK1.8
 */
@WebFilter(initParams = {@WebInitParam(name = "strfilter",value = "/str2")})
public class StringFilter extends DelegatingFilterProxy {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("strfilter filter dofilter");
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
