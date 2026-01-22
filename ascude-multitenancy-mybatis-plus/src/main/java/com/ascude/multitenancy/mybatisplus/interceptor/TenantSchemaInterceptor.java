package com.ascude.multitenancy.mybatisplus.interceptor;

import com.ascude.multitenancy.TenantContext;
import com.ascude.multitenancy.TenantIsolationLevel;
import com.ascude.multitenancy.config.TenantProperties;
import com.ascude.multitenancy.util.TenantPlaceholderResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

/**
 * MyBatis 租户 Schema 拦截器
 * 用于 Schema 级隔离，在执行 SQL 前切换数据库 Schema
 */
@Intercepts({
    @Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
    )
})
public class TenantSchemaInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(TenantSchemaInterceptor.class);

    private final TenantProperties tenantProperties;

    public TenantSchemaInterceptor(TenantProperties tenantProperties) {
        this.tenantProperties = tenantProperties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 只在 Schema 级隔离模式下生效
        if (tenantProperties.getIsolationLevel() != TenantIsolationLevel.SCHEMA) {
            return invocation.proceed();
        }

        String tenantId = TenantContext.getCurrentTenant();
        
        // 如果没有租户信息，使用默认租户
        if (StringUtils.isBlank(tenantId)) {
            tenantId = tenantProperties.getDefaultTenant();
        }

        // 解析 Schema 名称
        String schemaPattern = tenantProperties.getSchemaPattern();
        String schemaName = TenantPlaceholderResolver.resolveSchemaName(schemaPattern, tenantId);

        if (StringUtils.isNotBlank(schemaName)) {
            Connection connection = (Connection) invocation.getArgs()[0];
            
            try {
                // 切换 Schema
                switchSchema(connection, schemaName);
                logger.debug("切换到租户 Schema: {}", schemaName);
            } catch (Exception e) {
                logger.error("切换 Schema 失败: {}", schemaName, e);
                // 不中断执行，继续使用当前 Schema
            }
        }

        return invocation.proceed();
    }

    /**
     * 切换数据库 Schema
     */
    private void switchSchema(Connection connection, String schemaName) throws Exception {
        // 优先使用标准的 setSchema 方法（JDBC 4.1+）
        try {
            connection.setSchema(schemaName);
            return;
        } catch (AbstractMethodError | UnsupportedOperationException e) {
            // 某些驱动可能不支持 setSchema
            logger.debug("setSchema 方法不支持，尝试使用 USE 语句");
        }

        // 降级方案：使用 USE 语句（MySQL）
        try (Statement statement = connection.createStatement()) {
            statement.execute("USE " + schemaName);
        } catch (Exception e) {
            // 尝试 PostgreSQL 的 SET SCHEMA
            try (Statement statement = connection.createStatement()) {
                statement.execute("SET search_path TO " + schemaName);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以从配置中读取额外属性
    }
}
