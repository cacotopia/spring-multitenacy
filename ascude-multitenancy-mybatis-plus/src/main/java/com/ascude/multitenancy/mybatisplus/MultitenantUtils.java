package com.ascude.multitenancy.mybatisplus;

import com.ascude.multitenancy.mybatisplus.annotations.Multitenant;

public class MultitenantUtils {

    private MultitenantUtils() {
        // 工具类不允许实例化
    }

    /**
     * 检查实体类是否标注了 @Multitenant 注解
     */
    public static boolean isMultitenantEntity(Class<?> entityClass) {
        return entityClass.isAnnotationPresent(Multitenant.class);
    }

    /**
     * 获取实体类的 @Multitenant 注解
     */
    public static Multitenant getMultitenantAnnotation(Class<?> entityClass) {
        return entityClass.getAnnotation(Multitenant.class);
    }

    /**
     * 检查表是否应该被忽略（未标注 @Multitenant 或者 ignore=true）
     */
    public static boolean shouldIgnoreTable(Class<?> entityClass) {
        if (!isMultitenantEntity(entityClass)) {
            return true;
        }
        Multitenant annotation = getMultitenantAnnotation(entityClass);
        return annotation.ignore();
    }

}
