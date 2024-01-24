package org.union.sbp.springfragment.adaptor;

import org.osgi.framework.Bundle;
import org.union.sbp.springfragment.utils.SpringUnitUtil;
import org.union.sbp.springfragment.utils.UnitUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * osgi环境下的servletConfig子类,用于处理单servletContext下多单元的处理。
 *
 * @author youg
 * @since JDK1.8
 */
public class UnitServletConfig implements ServletConfig {

    private String contextPath;
    private String servletName;
    public UnitServletConfig(final String servletName){
        Bundle unit = UnitUtil.getBundle(null);
        contextPath = SpringUnitUtil.getUnitContextPath(this.getClass());
        this.servletName = servletName;
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return SpringUnitUtil.fetchServletContextFromOSGIService();
    }

    @Override
    public String getInitParameter(String name) {
        return this.getServletContext().getInitParameter(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return this.getServletContext().getInitParameterNames();
    }
}
