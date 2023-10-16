package org.union.sbp.springbase.adaptor.annoatation;

import java.lang.annotation.*;

/**
 * 单元配置注解，用于标识用于创建子context时最先加载到spring上下文，进行初始化相关配置.
 * @author youg
 * @since JDK1.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface UnitConfigComponent {
}
