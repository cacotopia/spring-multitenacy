package com.ascude.multitenancy.mybatisplus.handler;

import com.ascude.multitenancy.Tenant;
import com.ascude.multitenancy.TenantContext;
import com.ascude.multitenancy.config.TenantProperties;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

import java.util.HashSet;
import java.util.Set;

/**
 * MyBatis-Plus 租户行级处理器
 * 用于字段级隔离，自动在 SQL 中添加租户过滤条件
 */
public class MultitenancyTenantLineHandler implements TenantLineHandler {

    private final TenantProperties tenantProperties;
    private final Set<String> ignoreTables;

    public MultitenancyTenantLineHandler(TenantProperties tenantProperties) {
        this.tenantProperties = tenantProperties;
        this.ignoreTables = new HashSet<>(tenantProperties.getIgnoreTables());
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
     * 获取租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return tenantProperties.getTenantIdColumn();
    }

    /**
     * 判断表是否忽略租户过滤
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

        return false;
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
