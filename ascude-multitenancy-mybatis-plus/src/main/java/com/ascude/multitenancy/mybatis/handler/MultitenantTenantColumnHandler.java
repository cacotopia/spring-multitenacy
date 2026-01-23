package com.ascude.multitenancy.mybatis.handler;

import com.ascude.multitenancy.Tenant;
import com.ascude.multitenancy.TenantContext;
import com.ascude.multitenancy.TenantIsolationLevel;
import com.ascude.multitenancy.mybatis.EntityTenantInfo;
import com.ascude.multitenancy.mybatisplus.service.TenantConfigService;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;

import java.util.Map;

/**
 * 自定义多租户列处理器
 */
public class MultitenantTenantColumnHandler implements TenantLineHandler {

    private final String fieldName;
    private final Map<String, EntityTenantInfo> entityTenantInfoCache;
    private final TenantConfigService tenantConfigService;

    public MultitenantTenantColumnHandler(String fieldName, Map<String, EntityTenantInfo> entityTenantInfoCache,
                                          TenantConfigService tenantConfigService) {
        this.fieldName = fieldName;
        this.entityTenantInfoCache = entityTenantInfoCache;
        this.tenantConfigService = tenantConfigService;
    }

    @Override
    public Expression getTenantId() {
        Tenant tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            return new NullValue();
        }
        return new StringValue(tenant.getId());
    }

    @Override
    public String getTenantIdColumn() {
        // 从缓存中获取实体的租户信息
        return fieldName;
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // 这里可以添加忽略特定表的逻辑
        return false;
    }

    /**
     * 获取当前表的租户隔离级别
     */
    public TenantIsolationLevel getTableIsolationLevel(String tableName) {
        // 从缓存中获取实体的租户信息
        for (Map.Entry<String, EntityTenantInfo> entry : entityTenantInfoCache.entrySet()) {
            if (isTableMatchEntity(tableName, entry.getValue())) {
                return entry.getValue().getLevel();
            }
        }
        // 默认返回字段级隔离
        return TenantIsolationLevel.FIELD;
    }

    /**
     * 检查表名是否与实体匹配
     */
    private boolean isTableMatchEntity(String tableName, EntityTenantInfo tenantInfo) {
        // 这里需要实现表名与实体的匹配逻辑
        // 可以基于MyBatis-Plus的表名解析规则
        return true;
    }
}