package com.ascude.multitenancy.annotations;


import com.ascude.multitenancy.TenantIsolationLevel;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Multitenant {

    /**
     * 租户隔离级别
     */
    TenantIsolationLevel level() default TenantIsolationLevel.FIELD;

    /**
     * 字段级隔离：租户字段名（默认tenant_id）
     */
    String fieldName() default "tenant_id";

    /**
     * 表级隔离：表名前缀（如"t_{id}_"）
     */
    String tablePrefix() default "";

    /**
     * 表级隔离：表名后缀（如"_t_{id}"）
     */
    String tableSuffix() default "";

    /**
     * Schema级隔离：Schema名称模式（如"tenant_{id}"）
     */
    String schemaPattern() default "";

    /**
     * 是否忽略该实体的租户过滤
     */
    boolean ignore() default false;
}