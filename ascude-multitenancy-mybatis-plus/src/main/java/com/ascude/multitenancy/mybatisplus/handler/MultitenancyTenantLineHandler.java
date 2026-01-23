package com.ascude.multitenancy.mybatisplus.handler;

import com.ascude.multitenancy.Tenant;
import com.ascude.multitenancy.TenantContext;
import com.ascude.multitenancy.TenantIsolationLevel;
import com.ascude.multitenancy.config.TenantProperties;
import com.ascude.multitenancy.mybatis.EntityCache;
import com.ascude.multitenancy.mybatis.EntityTenantInfo;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

import java.util.HashSet;
import java.util.Set;

/**
 * MyBatis-Plus 租户行级处理器
 * 用于字段级隔离，自动在 SQL 中添加租户过滤条件
 * 支持根据实体注解动态判断是否应用字段级隔离
 */
public class MultitenancyTenantLineHandler implements TenantLineHandler {

    private final TenantProperties tenantProperties;
    private final Set<String> ignoreTables;
    private final EntityCache entityCache;

    public MultitenancyTenantLineHandler(TenantProperties tenantProperties, EntityCache entityCache) {
        this.tenantProperties = tenantProperties;
        this.ignoreTables = new HashSet<>(tenantProperties.getIgnoreTables());
        this.entityCache = entityCache;
    }

    /**
     * 获取租户ID的值表达式
     */
    @Override
    public Expression getTenantId() {
        Tenant currentTenant = TenantContext.getCurrentTenant();

        // 如果允许空租户且当前为空，返回空字符串
        if (currentTenant != null) {
            if (tenantProperties.isAllowEmptyTenant()) {
                return new StringValue("");
            }
            // 不允许空租户时返回不可能匹配的值
            return new StringValue("__NO_TENANT__");
        }

        return new StringValue(currentTenant.getId());
    }

    /**
     * 获取租户字段名（支持根据表名动态获取）
     */
    @Override
    public String getTenantIdColumn() {
        // 默认返回全局配置的字段名
        // 如果需要支持表级自定义字段名，可以从 ThreadLocal 中获取当前处理的表名
        return tenantProperties.getTenantIdColumn();
    }

    /**
     * 判断表是否忽略租户过滤
     * 现在支持根据实体注解判断：只有配置为 FIELD 级别隔离的表才应用此拦截器
     *
     * @param tableName 表名
     * @return true 表示忽略，不添加租户条件
     */
    @Override
    public boolean ignoreTable(String tableName) {
        // 检查是否在配置的忽略表列表中
        if (ignoreTables.contains(tableName)) {
            return true;
        }

        // 系统表默认忽略
        if (isSystemTable(tableName)) {
            return true;
        }
        
        // **核心逻辑**：根据实体的多租户配置判断是否应用字段级隔离
        EntityTenantInfo tenantInfo = entityCache.getEntityTenantInfoByTableName(tableName);
        
        // 如果实体配置为忽略多租户，则忽略
        if (tenantInfo.isIgnore()) {
            return true;
        }
        
        // 只有字段级隔离（FIELD）的表才需要应用 TenantLineHandler
        // 其他隔离级别（TABLE、SCHEMA、DATABASE）应该忽略
        return tenantInfo.getLevel() != TenantIsolationLevel.FIELD;
    }

    /**
     * 判断是否为系统表
     */
    private boolean isSystemTable(String tableName) {
        if (tableName == null) {
            return false;
        }
        String lowerTableName = tableName.toLowerCase();
        return lowerTableName.startsWith("sys_")
                || lowerTableName.startsWith("qrtz_")
                || lowerTableName.equals("schema_version");
    }

    /**
     * 添加忽略表
     */
    public void addIgnoreTable(String tableName) {
        this.ignoreTables.add(tableName);
    }

    /**
     * 批量添加忽略表
     */
    public void addIgnoreTables(String... tableNames) {
        for (String tableName : tableNames) {
            this.ignoreTables.add(tableName);
        }
    }
}
