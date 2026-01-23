package com.ascude.multitenancy.mybatisplus;

/**
 * 租户表配置信息
 */
public class TenantTableConfig {
    
    /**
     * 表名前缀
     */
    private String tablePrefix;
    
    /**
     * 表名后缀
     */
    private String tableSuffix;
    
    // getter and setter methods
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
}