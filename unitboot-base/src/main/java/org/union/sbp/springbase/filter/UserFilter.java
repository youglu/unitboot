package org.union.sbp.springbase.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 测试filter
 * @author youg
 * @since JDK1.8
 */
@Component
@WebFilter(filterName = "userFilter1",urlPatterns = "/*")
public class UserFilter implements Filter{
    public UserFilter(){
        System.out.println("init UserFilter");
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("user filter dofilter");
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
