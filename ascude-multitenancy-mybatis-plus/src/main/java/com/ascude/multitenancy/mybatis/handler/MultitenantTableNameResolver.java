package com.ascude.multitenancy.mybatis.handler;

import com.ascude.multitenancy.Tenant;
import com.ascude.multitenancy.TenantContext;
import com.ascude.multitenancy.model.TenantTableConfig;
import com.ascude.multitenancy.service.TenantConfigService;

/**
 * 多租户表名处理器
 */
public class MultitenantTableNameResolver {

    private final TenantConfigService tenantConfigService;

    public MultitenantTableNameResolver(TenantConfigService tenantConfigService) {
        this.tenantConfigService = tenantConfigService;
    }

    /**
     * 获取处理后的表名
     */
    public String getProcessedTableName(String originalTableName, Class<?> entityClass) {
        Tenant tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            return originalTableName;
        }

        // 获取动态表配置
        TenantTableConfig config = tenantConfigService.getTableConfig(tenant.getId(), entityClass.getName());

        if (config != null) {
            StringBuilder processedName = new StringBuilder();

            // 添加前缀
            if (config.getTablePrefix() != null && !config.getTablePrefix().isEmpty()) {
                String prefix = config.getTablePrefix().replace("{id}", tenant.getId());
                processedName.append(prefix);
            }

            processedName.append(originalTableName);

            // 添加后缀
            if (config.getTableSuffix() != null && !config.getTableSuffix().isEmpty()) {
                String suffix = config.getTableSuffix().replace("{id}", tenant.getId());
                processedName.append(suffix);
            }

            return processedName.toString();
        }

        return originalTableName;
    }
}