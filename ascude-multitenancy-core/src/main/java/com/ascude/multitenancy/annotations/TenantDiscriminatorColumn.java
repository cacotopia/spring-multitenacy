package com.ascude.multitenancy.annotations;

import java.lang.annotation.*;

/**
 * 字段级别的租户标识注解
 * 标注在实体字段上，指定该字段为租户ID字段（仅字段级隔离有效）
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantDiscriminatorColumn {

}

//public @interface TenantField {
//    // 无参数，仅作为标识
//}