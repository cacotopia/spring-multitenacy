package com.ascude.multitenancy.mybatisplus.config;

import com.ascude.multitenancy.TenantIsolationLevel;
import com.ascude.multitenancy.config.TenantProperties;
import com.ascude.multitenancy.mybatisplus.handler.MultitenancyTableNameHandler;
import com.ascude.multitenancy.mybatisplus.handler.MultitenancyTenantLineHandler;
import com.ascude.multitenancy.mybatisplus.interceptor.TenantSchemaInterceptor;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * MyBatis-Plus 多租户插件配置器
 * 根据隔离级别自动配置相应的拦截器
 */
public class MybatisPlusMultitenancyConfigurer {

    private final TenantProperties tenantProperties;

    public MybatisPlusMultitenancyConfigurer(TenantProperties tenantProperties) {
        this.tenantProperties = tenantProperties;
    }

    /**
     * 配置 MyBatis-Plus 拦截器
     *
     * @return MybatisPlusInterceptor
     */
    public MybatisPlusInterceptor configureMybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 根据隔离级别配置不同的拦截器
        TenantIsolationLevel isolationLevel = tenantProperties.getIsolationLevel();

        switch (isolationLevel) {
            case FIELD:
                // 字段级隔离：使用租户行处理器
                configureTenantLineInterceptor(interceptor);
                break;

            case TABLE:
                // 表级隔离：使用动态表名处理器
                configureDynamicTableNameInterceptor(interceptor);
                break;

            case SCHEMA:
                // Schema级隔离：通过独立拦截器处理
                // TenantSchemaInterceptor 需要单独注册到 SqlSessionFactory
                break;

            case DATABASE:
                // 数据库级隔离：通过动态数据源处理，无需 MyBatis 层拦截
                break;

            default:
                throw new IllegalArgumentException("不支持的隔离级别: " + isolationLevel);
        }

        return interceptor;
    }

    /**
     * 配置租户行级拦截器（字段级隔离）
     */
    private void configureTenantLineInterceptor(MybatisPlusInterceptor interceptor) {
        MultitenancyTenantLineHandler tenantLineHandler = new MultitenancyTenantLineHandler(tenantProperties);
        TenantLineInnerInterceptor tenantLineInterceptor = new TenantLineInnerInterceptor(tenantLineHandler);
        interceptor.addInnerInterceptor(tenantLineInterceptor);
    }

    /**
     * 配置动态表名拦截器（表级隔离）
     */
    private void configureDynamicTableNameInterceptor(MybatisPlusInterceptor interceptor) {
        DynamicTableNameInnerInterceptor dynamicTableNameInterceptor = new DynamicTableNameInnerInterceptor();

        // 配置表名处理器
        Map<String, MultitenancyTableNameHandler> tableNameHandlerMap = new HashMap<>();
        MultitenancyTableNameHandler tableNameHandler = new MultitenancyTableNameHandler(tenantProperties);

        // 这里可以配置具体需要处理的表，如果为空则处理所有表
        // tableNameHandlerMap.put("user", tableNameHandler);
        // tableNameHandlerMap.put("product", tableNameHandler);

        // 使用通用处理器处理所有表
//        dynamicTableNameInterceptor.setTableNameHandler((sql, tableName) ->
//                tableNameHandler.dynamicTableName(sql, tableName)
//        );

        interceptor.addInnerInterceptor(dynamicTableNameInterceptor);
    }

    /**
     * 创建 Schema 拦截器（需要单独注册）
     */
    public TenantSchemaInterceptor createSchemaInterceptor() {
        return new TenantSchemaInterceptor(tenantProperties);
    }

    /**
     * 创建租户行处理器
     */
    public MultitenancyTenantLineHandler createTenantLineHandler() {
        return new MultitenancyTenantLineHandler(tenantProperties);
    }

    /**
     * 创建表名处理器
     */
    public MultitenancyTableNameHandler createTableNameHandler() {
        return new MultitenancyTableNameHandler(tenantProperties);
    }
}
