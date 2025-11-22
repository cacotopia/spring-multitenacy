package com.ascude.multitenancy.mybatis;

import com.ascude.multitenancy.TenantIsolationLevel;

/**
 * 实体类租户信息
 */
public class EntityTenantInfo {

    private TenantIsolationLevel level;
    private String fieldName;
    private String tablePrefix;
    private String tableSuffix;
    private String schemaPattern;
    private boolean ignore;

    // getter and setter methods
    public TenantIsolationLevel getLevel() {
        return level;
    }

    public void setLevel(TenantIsolationLevel level) {
        this.level = level;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
}