package com.ascude.multitenancy.mybatis.autoconfigure;

import com.ascude.multitenancy.config.TenantProperties;
import com.ascude.multitenancy.mybatis.EntityCache;
import com.ascude.multitenancy.mybatis.EntityScanner;
import com.ascude.multitenancy.mybatis.MultitenantConfig;
import com.ascude.multitenancy.mybatis.handler.MultitenantTenantColumnHandler;
import com.ascude.multitenancy.mybatis.handler.MultitenantTableNameHandler;
import com.ascude.multitenancy.mybatis.handler.MultitenantTableNameResolver;
import com.ascude.multitenancy.mybatisplus.handler.MultitenancyTableNameHandler;
import com.ascude.multitenancy.mybatisplus.handler.MultitenancyTenantLineHandler;
import com.ascude.multitenancy.mybatisplus.interceptor.TenantSchemaInterceptor;
import com.ascude.multitenancy.mybatisplus.service.TenantConfigService;
import com.ascude.multitenancy.mybatisplus.service.impl.DefaultTenantConfigService;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatis-Plus多租户自动配置类
 * 支持根据实体注解混合使用多种隔离策略
 */
@AutoConfiguration
@EnableConfigurationProperties({MultitenantProperties.class, TenantProperties.class})
public class MultitenancyMybatisPlusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MultitenantConfig multitenantConfig(MultitenantProperties properties) {
        MultitenantConfig config = new MultitenantConfig();
        config.setDefaultLevel(properties.getDefaultLevel());
        config.setDefaultFieldName(properties.getDefaultFieldName());
        config.setGlobalSchemaPattern(properties.getGlobalSchemaPattern());
        config.setGlobalDatabaseUrlPattern(properties.getGlobalDatabaseUrlPattern());
        config.setDatabaseUsername(properties.getDatabaseUsername());
        config.setDatabasePassword(properties.getDatabasePassword());
        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public EntityCache entityCache(MultitenantConfig multitenantConfig, TenantConfigService tenantConfigService) {
        return new EntityCache(multitenantConfig, tenantConfigService);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public TenantConfigService tenantConfigService() {
        return new DefaultTenantConfigService();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public TenantProperties tenantProperties() {
        return new TenantProperties();
    }
    
    /**
     * 实体扫描器，在应用启动时扫描并注册所有实体
     */
    @Bean
    public EntityScanner entityScanner(EntityCache entityCache, MultitenantProperties properties) {
        EntityScanner scanner = new EntityScanner(entityCache);
        
        // 如果配置了扫描路径，则进行扫描
        if (properties.getEntityScanPackages() != null && properties.getEntityScanPackages().length > 0) {
            scanner.scanAndRegister(properties.getEntityScanPackages());
        }
        
        return scanner;
    }

    @Bean
    @ConditionalOnMissingBean
    public MultitenantTableNameResolver multitenantTableNameResolver(TenantConfigService tenantConfigService) {
        return new MultitenantTableNameResolver(tenantConfigService);
    }

    @Bean
    @ConditionalOnMissingBean
    public MultitenantTenantColumnHandler tenantLineHandler(MultitenantConfig multitenantConfig, 
                                                           EntityCache entityCache, 
                                                           TenantConfigService tenantConfigService) {
        return new MultitenantTenantColumnHandler(
                multitenantConfig.getDefaultFieldName(), 
                new ConcurrentHashMap<>(), 
                tenantConfigService);
    }

    @Bean
    @ConditionalOnMissingBean
    public MultitenantTableNameHandler tableNameHandler(MultitenantTableNameResolver tableNameResolver, 
                                                       EntityCache entityCache) {
        return new MultitenantTableNameHandler(tableNameResolver, entityCache);
    }

    /**
     * 配置 MyBatis-Plus 拦截器
     * 同时支持字段级隔离和表级隔离，根据实体注解动态路由
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(
            TenantProperties tenantProperties,
            EntityCache entityCache,
            SqlSessionFactory sqlSessionFactory) {
        
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 1. 添加租户行级拦截器（字段级隔离 FIELD）
        MultitenancyTenantLineHandler tenantLineHandler = new MultitenancyTenantLineHandler(tenantProperties, entityCache);
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(tenantLineHandler));
        
        // 2. 添加动态表名拦截器（表级隔离 TABLE）
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        MultitenancyTableNameHandler tableNameHandler = new MultitenancyTableNameHandler(tenantProperties, entityCache);
        dynamicTableNameInnerInterceptor.setTableNameHandler(tableNameHandler);
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        
        // 3. 添加 Schema 级隔离拦截器（SCHEMA）
        TenantSchemaInterceptor schemaInterceptor = new TenantSchemaInterceptor(tenantProperties, entityCache);
        sqlSessionFactory.getConfiguration().addInterceptor(schemaInterceptor);
        
        return interceptor;
    }
}