package org.union.sbp.springbase.adaptor.web;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

/**
 * 单元的filter注册器，用于将子单元的filter注册到主context,因为filter是全局性的，共用一个web容器，
 * 所以需要在主context中注入到web容器中运行.
 * @author youg
 * @since JDK1.8
 */
public class UnitFilterRegistrationBean extends FilterRegistrationBean {
    /**
     * 所属单元名称.
     */
    private String unitName;

    /**
     * 构造器
     * @param unitName 单元名
     * @param filter 过滤器
     */
    public UnitFilterRegistrationBean(final String unitName,final Filter filter) {
        super(filter);
        this.unitName = unitName;
    }

    /**
     * 注册filter
     * @param description
     * @param servletContext
     * @return
     */
    @Override
    public FilterRegistration.Dynamic addRegistration(String description, ServletContext servletContext) {
        return super.addRegistration(description, servletContext);
    }

    /**
     * 获得所属单元名称.
     * @return
     */
    public String getUnitName() {
        return unitName;
    }
}
