package com.ascude.multitenancy.config;

import com.ascude.multitenancy.TenantIsolationLevel;

import java.util.Collections;
import java.util.List;

/**
 * 租户配置属性
 */
public class TenantProperties {

    /**
     * 是否启用多租户
     */
    private boolean enabled = true;

    /**
     * 默认租户ID
     */
    private String defaultTenant = "default";

    /**
     * 租户隔离级别
     */
    private TenantIsolationLevel isolationLevel = TenantIsolationLevel.FIELD;

    /**
     * 字段级隔离：租户字段名
     */
    private String tenantIdColumn = "tenant_id";

    /**
     * 表级隔离：表名前缀模板（如：t_{id}_）
     */
    private String tablePrefix = "";

    /**
     * 表级隔离：表名后缀模板（如：_t_{id}）
     */
    private String tableSuffix = "";

    /**
     * Schema级隔离：Schema名称模板（如：tenant_{id}）
     */
    private String schemaPattern = "tenant_{id}";

    /**
     * 忽略多租户处理的表名列表
     */
    private List<String> ignoreTables = Collections.emptyList();

    /**
     * 忽略多租户处理的URL路径列表
     */
    private List<String> ignoreUrls = Collections.emptyList();

    /**
     * 当租户未找到时是否抛出异常
     */
    private boolean throwExceptionOnMissingTenant = true;

    /**
     * 是否允许空租户访问（用于公共数据访问）
     */
    private boolean allowEmptyTenant = false;

    // Getters and Setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultTenant() {
        return defaultTenant;
    }

    public void setDefaultTenant(String defaultTenant) {
        this.defaultTenant = defaultTenant;
    }

    public TenantIsolationLevel getIsolationLevel() {
        return isolationLevel;
    }

    public void setIsolationLevel(TenantIsolationLevel isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    public void setTenantIdColumn(String tenantIdColumn) {
        this.tenantIdColumn = tenantIdColumn;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String getTableSuffix() {
        return tableSuffix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    public String getSchemaPattern() {
        return schemaPattern;
    }

    public void setSchemaPattern(String schemaPattern) {
        this.schemaPattern = schemaPattern;
    }

    public List<String> getIgnoreTables() {
        return ignoreTables;
    }

    public void setIgnoreTables(List<String> ignoreTables) {
        this.ignoreTables = ignoreTables;
    }

    public List<String> getIgnoreUrls() {
        return ignoreUrls;
    }

    public void setIgnoreUrls(List<String> ignoreUrls) {
        this.ignoreUrls = ignoreUrls;
    }

    public boolean isThrowExceptionOnMissingTenant() {
        return throwExceptionOnMissingTenant;
    }

    public void setThrowExceptionOnMissingTenant(boolean throwExceptionOnMissingTenant) {
        this.throwExceptionOnMissingTenant = throwExceptionOnMissingTenant;
    }

    public boolean isAllowEmptyTenant() {
        return allowEmptyTenant;
    }

    public void setAllowEmptyTenant(boolean allowEmptyTenant) {
        this.allowEmptyTenant = allowEmptyTenant;
    }
}
