package com.ascude.multitenancy.mybatis.autoconfigure;

import com.ascude.multitenancy.TenantIsolationLevel;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = Constants.MYBATIS_PLUS)
public class MultitenantProperties {

    /**
     * 全局默认隔离级别
     */
    private TenantIsolationLevel defaultLevel = TenantIsolationLevel.FIELD;

    /**
     * 全局默认租户字段名
     */
    private String defaultFieldName = "tenant_id";

    /**
     * 全局Schema模式（仅Schema级别隔离有效）
     */
    private String globalSchemaPattern = "tenant_{id}";

    /**
     * 全局数据库URL模式（仅Database级别隔离有效）
     */
    private String globalDatabaseUrlPattern = "jdbc:mysql://localhost:3306/tenant_{id}?useUnicode=true&characterEncoding=utf8";

    /**
     * 数据库用户名（仅Database级别隔离有效）
     */
    private String databaseUsername;

    /**
     * 数据库密码（仅Database级别隔离有效）
     */
    private String databasePassword;

    // getter and setter methods
    public TenantIsolationLevel getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(TenantIsolationLevel defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public String getDefaultFieldName() {
        return defaultFieldName;
    }

    public void setDefaultFieldName(String defaultFieldName) {
        this.defaultFieldName = defaultFieldName;
    }

    public String getGlobalSchemaPattern() {
        return globalSchemaPattern;
    }

    public void setGlobalSchemaPattern(String globalSchemaPattern) {
        this.globalSchemaPattern = globalSchemaPattern;
    }

    public String getGlobalDatabaseUrlPattern() {
        return globalDatabaseUrlPattern;
    }

    public void setGlobalDatabaseUrlPattern(String globalDatabaseUrlPattern) {
        this.globalDatabaseUrlPattern = globalDatabaseUrlPattern;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }
}
