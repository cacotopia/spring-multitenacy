package com.ascude.multitenancy.mybatisplus.service;

import com.ascude.multitenancy.mybatisplus.TenantTableConfig;

/**
 * 租户配置服务接口
 * 用于动态获取租户的表名前缀和后缀配置
 */
public interface TenantConfigService {

    /**
     * 获取指定租户和实体类的表配置信息
     *
     * @param tenantId        租户ID
     * @param entityClassName 实体类名称
     * @return 租户表配置信息，如果没有配置则返回null
     */
    TenantTableConfig getTableConfig(String tenantId, String entityClassName);

    /**
     * 获取指定租户的默认表配置信息
     *
     * @param tenantId 租户ID
     * @return 默认租户表配置信息，如果没有配置则返回null
     */
    TenantTableConfig getDefaultTableConfig(String tenantId);
}