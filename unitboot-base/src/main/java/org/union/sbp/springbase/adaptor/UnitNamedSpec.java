package org.union.sbp.springbase.adaptor;

import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * 子context配置类设置类，用于根据子context的名称保存初始化子context时的配置类。
 * @author youg
 * @since JDK1.8
 */
public class UnitNamedSpec implements NamedContextFactory.Specification {

    /**
     * 子context名称（需与单元名称一致)
     */
    private final String subContextName;
    /**
     * 用于初始化子context的配置类集合.
     */
    private final Class<?>[] configuration;

    /**
     * 构造器
     * @param subContextName 子context名称
     * @param configuration
     */
    public UnitNamedSpec(final String subContextName,final Class<?>[] configuration) {
        this.subContextName = subContextName;
        this.configuration = configuration;
    }

    /**
     * 获得子context名称
     * @return
     */
    @Override
    public String getName() {
        return subContextName;
    }

    /**
     * 获得初始化配置类集合.
     * @return
     */
    @Override
    public Class<?>[] getConfiguration() {
        return configuration;
    }
}
