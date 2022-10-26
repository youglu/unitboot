package org.union.sbp.springbase.adaptor.web;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

public class UnitFilterRegistrationBean extends FilterRegistrationBean {
    /**
     * 所属单元名称.
     */
    private String unitName;
    public UnitFilterRegistrationBean(final String unitName,final Filter filter) {
        super(filter);
        this.unitName = unitName;
    }
    @Override
    public FilterRegistration.Dynamic addRegistration(String description, ServletContext servletContext) {
        return super.addRegistration(description, servletContext);
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
