package com.ascude.multitenancy.mybatis.autoconfigure;

import com.ascude.multitenancy.mybatis.EntityCache;
import com.ascude.multitenancy.mybatis.MultitenantConfig;
import com.ascude.multitenancy.mybatis.handler.MultitenantTenantColumnHandler;
import com.ascude.multitenancy.mybatis.handler.MultitenantTableNameHandler;
import com.ascude.multitenancy.mybatis.handler.MultitenantTableNameResolver;
import com.ascude.multitenancy.service.TenantConfigService;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatis-Plus多租户自动配置类
 */
@AutoConfiguration
@EnableConfigurationProperties(MultitenantProperties.class)
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
        EntityCache entityCache = new EntityCache(multitenantConfig,tenantConfigService);
//        entityCache.setMultitenantConfig(multitenantConfig);
//        entityCache.setTenantConfigService(tenantConfigService);
        return entityCache;
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

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(MultitenantTenantColumnHandler tenantLineHandler, 
                                                       MultitenantTableNameHandler tableNameHandler) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 添加租户行级拦截器
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(tenantLineHandler));
        
        // 添加动态表名拦截器
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler(tableNameHandler);
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        
        return interceptor;
    }
}