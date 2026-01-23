package com.ascude.multitenancy.service.impl;

import com.ascude.multitenancy.model.TenantTableConfig;
import com.ascude.multitenancy.service.TenantConfigService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认租户配置服务实现
 * 实际项目中可以扩展为从数据库或缓存中读取配置
 */
public class DefaultTenantConfigService implements TenantConfigService {

    // 模拟存储租户配置信息
    private final Map<String, Map<String, TenantTableConfig>> tenantConfigs = new ConcurrentHashMap<>();

    @Override
    public TenantTableConfig getTableConfig(String tenantId, String entityClassName) {
        // 从存储中获取指定租户和实体类的配置
        Map<String, TenantTableConfig> entityConfigs = tenantConfigs.get(tenantId);
        if (entityConfigs != null) {
            return entityConfigs.get(entityClassName);
        }
        return null;
    }

    @Override
    public TenantTableConfig getDefaultTableConfig(String tenantId) {
        // 从存储中获取指定租户的默认配置
        // 这里可以实现为从数据库、缓存或配置文件中读取
        Map<String, TenantTableConfig> entityConfigs = tenantConfigs.get(tenantId);
        if (entityConfigs != null) {
            return entityConfigs.get("default");
        }
        return null;
    }

    /**
     * 注册租户配置（实际项目中可以通过管理接口调用）
     */
    public void registerTenantConfig(String tenantId, String entityClassName, String tablePrefix, String tableSuffix) {
        Map<String, TenantTableConfig> entityConfigs = tenantConfigs.computeIfAbsent(tenantId, k -> new ConcurrentHashMap<>());

        TenantTableConfig config = new TenantTableConfig();
        config.setTablePrefix(tablePrefix);
        config.setTableSuffix(tableSuffix);

        entityConfigs.put(entityClassName, config);
    }
}