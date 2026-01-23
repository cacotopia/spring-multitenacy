package com.ascude.multitenancy.mybatisplus.handler;

import com.ascude.multitenancy.Tenant;
import com.ascude.multitenancy.TenantContext;
import com.ascude.multitenancy.TenantIsolationLevel;
import com.ascude.multitenancy.config.TenantProperties;
import com.ascude.multitenancy.mybatis.EntityCache;
import com.ascude.multitenancy.mybatis.EntityTenantInfo;
import com.ascude.multitenancy.util.TenantPlaceholderResolver;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MyBatis-Plus 动态表名处理器
 * 用于表级隔离，自动在表名前后添加租户相关前后缀
 */
public class MultitenancyTableNameHandler implements TableNameHandler {

    private static final Logger logger = LoggerFactory.getLogger(MultitenancyTableNameHandler.class);

    private final TenantProperties tenantProperties;
    private final EntityCache entityCache;

    public MultitenancyTableNameHandler(TenantProperties tenantProperties, EntityCache entityCache) {
        this.tenantProperties = tenantProperties;
        this.entityCache = entityCache;
    }

    /**
     * 动态解析表名
     * 根据实体的多租户配置决定是否应用表级隔离
     *
     * @param sql       当前执行的 SQL
     * @param tableName 原始表名
     * @return 解析后的表名
     */
    @Override
    public String dynamicTableName(String sql, String tableName) {
        // 获取当前表的多租户配置
        EntityTenantInfo tenantInfo = entityCache.getEntityTenantInfoByTableName(tableName);
        
        // 如果实体配置为忽略多租户，直接返回原始表名
        if (tenantInfo.isIgnore()) {
            return tableName;
        }
        
        // 只有表级隔离（TABLE）才应用此处理器
        if (tenantInfo.getLevel() != TenantIsolationLevel.TABLE) {
            return tableName;
        }

        Tenant currentTenant = TenantContext.getCurrentTenant();

        // 如果没有租户信息，返回原始表名
        if (currentTenant == null) {
            return tableName;
        }

        // 优先使用实体级别的配置，其次使用全局配置
        String prefix = StringUtils.isNotBlank(tenantInfo.getTablePrefix()) 
                ? tenantInfo.getTablePrefix() 
                : tenantProperties.getTablePrefix();
        String suffix = StringUtils.isNotBlank(tenantInfo.getTableSuffix()) 
                ? tenantInfo.getTableSuffix() 
                : tenantProperties.getTableSuffix();

        // 如果前缀和后缀都为空，返回原始表名
        if (StringUtils.isBlank(prefix) && StringUtils.isBlank(suffix)) {
            return tableName;
        }

        // 解析表名
        String resolvedTableName = TenantPlaceholderResolver.resolveTableName(
                tableName, prefix, suffix, currentTenant.getId());
        
        logger.debug("Dynamic table name: {} -> {}", tableName, resolvedTableName);
        
        return resolvedTableName;
    }
}
