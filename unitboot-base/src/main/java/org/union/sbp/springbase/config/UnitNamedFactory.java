package org.union.sbp.springbase.config;

import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于根据单元名称创建子context。
 * @since JDK1.8
 * @author youg
 */
public class UnitNamedFactory extends NamedContextFactory<UnitNamedSpec> {
    /**
     * 反射获得父类的context MAP。
     */
    private Map<String, AnnotationConfigApplicationContext> contexts = null;

    /**
     * 构造器.
     * @param defaultConfigType
     * @param contextNam
     * @param propertyName
     */
    public UnitNamedFactory(Class<?> defaultConfigType, String contextNam, String propertyName) {
        super(defaultConfigType,contextNam,propertyName);
        try {
            Field contextsField = NamedContextFactory.class.getDeclaredField("contexts");
            contextsField.setAccessible(true);
            contexts = (Map)contextsField.get(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据context名称关闭context。
     * @param name
     */
    public void closeContext(String name) {
        AnnotationConfigApplicationContext context = getContextByName(name);
        if (null != context) {
            context.close();
            contexts.remove(name);
        }
    }

    /**
     * 根拓Context名称获得子Context，不存在不会创建.
     * @param name
     * @return
     */
    public AnnotationConfigApplicationContext getContextByName(final String name) {
        if (this.contexts.containsKey(name)) {
            return contexts.get(name);
        }
        return null;
    }

    /**
     * 根据Context名称获得context，不存在会创建.
     * @param name
     * @return
     */
    public AnnotationConfigApplicationContext getContextWithCreate(final String name) {
        return super.getContext(name);
    }

}