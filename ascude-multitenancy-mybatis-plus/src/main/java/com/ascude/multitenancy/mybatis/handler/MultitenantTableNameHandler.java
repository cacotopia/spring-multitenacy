package com.ascude.multitenancy.mybatis.handler;

import com.ascude.multitenancy.mybatis.EntityCache;
import com.ascude.multitenancy.mybatis.EntityTenantInfo;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;

/**
 * 自定义表名处理器，用于表级租户隔离
 */
public class MultitenantTableNameHandler implements TableNameHandler {

    private MultitenantTableNameResolver multitenantTableNameResolver;

    private EntityCache entityCache;

    public MultitenantTableNameHandler(MultitenantTableNameResolver multitenantTableNameResolver,
                                       EntityCache entityCache) {
        this.multitenantTableNameResolver = multitenantTableNameResolver;
        this.entityCache = entityCache;
    }

    @Override
    public String dynamicTableName(String sql, String tableName) {
        // 这里需要根据sql和表名推断出对应的实体类
        // 实际实现中可能需要维护表名和实体类的映射关系
        Class<?> entityClass = getEntityClassByTableName(tableName);

        if (entityClass != null) {
            EntityTenantInfo tenantInfo = entityCache.getEntityTenantInfo(entityClass);
            if (tenantInfo != null && tenantInfo.getLevel() == com.ascude.multitenancy.TenantIsolationLevel.TABLE) {
                return multitenantTableNameResolver.getProcessedTableName(tableName, entityClass);
            }
        }

        return tableName;
    }

    /**
     * 根据表名获取对应的实体类
     */
    private Class<?> getEntityClassByTableName(String tableName) {
        // 这里需要实现表名到实体类的映射逻辑
        // 可以基于命名规则或维护一个映射表
        return null;
    }
}