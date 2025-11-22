package com.ascude.multitenancy.mybatis;

import com.ascude.multitenancy.Tenant;
import com.ascude.multitenancy.TenantContext;
import com.ascude.multitenancy.TenantIsolationLevel;
import com.ascude.multitenancy.annotations.Multitenant;
import com.ascude.multitenancy.model.TenantTableConfig;
import com.ascude.multitenancy.service.TenantConfigService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类多租户配置缓存
 */
public class EntityCache {

    private MultitenantConfig multitenantConfig;

    private TenantConfigService tenantConfigService;

    public EntityCache(MultitenantConfig config, TenantConfigService configService) {
        this.multitenantConfig = config;
        this.tenantConfigService = configService;
    }

    private final ConcurrentHashMap<Class<?>, EntityTenantInfo> entityInfoCache = new ConcurrentHashMap<>();

    /**
     * 获取实体类的多租户配置信息
     */
    public EntityTenantInfo getEntityTenantInfo(Class<?> entityClass) {
        return entityInfoCache.computeIfAbsent(entityClass, this::buildEntityTenantInfo);
    }

    /**
     * 获取实体类的多租户配置信息（包含动态表配置）
     * 当使用表级隔离时，会从租户服务获取动态配置
     */
    public EntityTenantInfo getEntityTenantInfoWithDynamicConfig(Class<?> entityClass) {
        // 获取基础配置
        EntityTenantInfo info = getEntityTenantInfo(entityClass);

        // 如果是表级隔离且当前有租户上下文，尝试获取动态配置
        if (info.getLevel() == TenantIsolationLevel.TABLE && TenantContext.getCurrentTenant() != null) {
            Tenant tenantCurrent = TenantContext.getCurrentTenant();
            String entityClassName = entityClass.getName();

            // 尝试获取实体类特定的配置
            TenantTableConfig dynamicConfig = tenantConfigService.getTableConfig(tenantCurrent.getId(), entityClassName);

            // 如果没有实体类特定的配置，尝试获取默认配置
            if (dynamicConfig == null) {
                dynamicConfig = tenantConfigService.getDefaultTableConfig(tenantCurrent.getId());
            }

            // 如果有动态配置，创建一个新的配置实例（避免修改缓存的基础配置）
            if (dynamicConfig != null) {
                EntityTenantInfo dynamicInfo = new EntityTenantInfo();
                dynamicInfo.setLevel(info.getLevel());
                dynamicInfo.setFieldName(info.getFieldName());
                dynamicInfo.setTablePrefix(dynamicConfig.getTablePrefix());
                dynamicInfo.setTableSuffix(dynamicConfig.getTableSuffix());
                dynamicInfo.setSchemaPattern(info.getSchemaPattern());
                dynamicInfo.setIgnore(info.isIgnore());
                return dynamicInfo;
            }
        }

        // 返回基础配置或合并后的配置
        return info;
    }

    private EntityTenantInfo buildEntityTenantInfo(Class<?> entityClass) {
        Multitenant multitenant = entityClass.getAnnotation(Multitenant.class);
        EntityTenantInfo info = new EntityTenantInfo();

        if (multitenant != null) {
            info.setLevel(multitenant.level());
            info.setFieldName(multitenant.fieldName());
            info.setTablePrefix(multitenant.tablePrefix());
            info.setTableSuffix(multitenant.tableSuffix());
            info.setSchemaPattern(multitenant.schemaPattern());
            info.setIgnore(multitenant.ignore());
        } else {
            // 使用全局默认配置
            info.setLevel(multitenantConfig.getDefaultLevel());
            info.setFieldName(multitenantConfig.getDefaultFieldName());
            info.setIgnore(false);
        }

        // Schema和Database级别使用全局模式
        if (info.getLevel() == TenantIsolationLevel.SCHEMA && info.getSchemaPattern().isEmpty()) {
            info.setSchemaPattern(multitenantConfig.getGlobalSchemaPattern());
        }

        return info;
    }

    // 提供方法来更新实体租户信息缓存
    public void updateEntityTenantInfo(Class<?> entityClass, EntityTenantInfo tenantInfo) {
        entityInfoCache.put(entityClass, tenantInfo);
    }

}