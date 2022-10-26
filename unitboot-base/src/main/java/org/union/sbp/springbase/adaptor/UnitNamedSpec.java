package org.union.sbp.springbase.adaptor;

import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * @author tianwen.yin
 */
public class UnitNamedSpec implements NamedContextFactory.Specification {

    private final String name;
    private final Class<?>[] configuration;

    public UnitNamedSpec(String name, Class<?>[] configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return configuration;
    }
}
