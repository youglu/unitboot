package org.union.sbp.springfragment.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * osgi环境下的springboot应用条件类
 */
public class OnOsgiEnvCondition implements Condition {
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return null != System.getProperty("osgi.adaptor");
    }
}