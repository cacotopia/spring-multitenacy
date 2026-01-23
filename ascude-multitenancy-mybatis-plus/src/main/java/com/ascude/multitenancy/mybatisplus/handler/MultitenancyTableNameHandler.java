package com.ascude.multitenancy.mybatisplus.handler;

import com.ascude.multitenancy.Tenant;
import com.ascude.multitenancy.TenantContext;
import com.ascude.multitenancy.TenantIsolationLevel;
import com.ascude.multitenancy.config.TenantProperties;
import com.ascude.multitenancy.util.TenantPlaceholderResolver;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * MyBatis-Plus 动态表名处理器
 * 用于表级隔离，自动在表名前后添加租户相关前后缀
 */
public class MultitenancyTableNameHandler implements TableNameHandler {

    private final TenantProperties tenantProperties;

    public MultitenancyTableNameHandler(TenantProperties tenantProperties) {
        this.tenantProperties = tenantProperties;
    }

    /**
     * 动态解析表名
     *
     * @param sql       当前执行的 SQL
     * @param tableName 原始表名
     * @return 解析后的表名
     */
    @Override
    public String dynamicTableName(String sql, String tableName) {
        // 只在表级隔离模式下生效
        if (tenantProperties.getIsolationLevel() != TenantIsolationLevel.TABLE) {
            return tableName;
        }

        Tenant currentTenant = TenantContext.getCurrentTenant();

        // 如果没有租户信息，返回原始表名
        if (currentTenant == null) {
            return tableName;
        }

        // 获取表名前缀和后缀
        String prefix = tenantProperties.getTablePrefix();
        String suffix = tenantProperties.getTableSuffix();

        // 如果前缀和后缀都为空，返回原始表名
        if (StringUtils.isBlank(prefix) && StringUtils.isBlank(suffix)) {
            return tableName;
        }

        // 解析表名
        return TenantPlaceholderResolver.resolveTableName(tableName, prefix, suffix, currentTenant.getId());
    }
}
