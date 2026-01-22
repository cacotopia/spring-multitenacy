package com.ascude.multitenancy.mybatisplus.config;

import com.ascude.multitenancy.TenantIsolationLevel;
import com.ascude.multitenancy.config.TenantProperties;
import com.ascude.multitenancy.mybatisplus.interceptor.TenantSchemaInterceptor;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * MyBatis-Plus 多租户配置示例
 * 
 * 使用方法：
 * 1. 在 Spring 配置类中创建 TenantProperties Bean
 * 2. 使用本类配置 MyBatis-Plus 插件
 * 3. 注册到 Spring 容器中
 */
public class MybatisPlusMultitenancyConfiguration {

    /**
     * 配置 MyBatis-Plus 拦截器 Bean
     * 
     * 在 Spring Boot 中使用：
     * <pre>
     * {@code
     * @Bean
     * public MybatisPlusInterceptor mybatisPlusInterceptor(TenantProperties tenantProperties) {
     *     MybatisPlusMultitenancyConfigurer configurer = 
     *         new MybatisPlusMultitenancyConfigurer(tenantProperties);
     *     return configurer.configureMybatisPlusInterceptor();
     * }
     * }
     * </pre>
     */
    public static MybatisPlusInterceptor createInterceptor(TenantProperties tenantProperties) {
        MybatisPlusMultitenancyConfigurer configurer = 
            new MybatisPlusMultitenancyConfigurer(tenantProperties);
        return configurer.configureMybatisPlusInterceptor();
    }

    /**
     * 配置 Schema 拦截器（仅在 SCHEMA 隔离级别下使用）
     * 
     * 需要手动注册到 SqlSessionFactory：
     * <pre>
     * {@code
     * @Bean
     * public SqlSessionFactory sqlSessionFactory(DataSource dataSource, 
     *                                            TenantProperties tenantProperties) throws Exception {
     *     SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
     *     factoryBean.setDataSource(dataSource);
     *     
     *     if (tenantProperties.getIsolationLevel() == TenantIsolationLevel.SCHEMA) {
     *         TenantSchemaInterceptor schemaInterceptor = 
     *             MybatisPlusMultitenancyConfiguration.createSchemaInterceptor(tenantProperties);
     *         factoryBean.setPlugins(schemaInterceptor);
     *     }
     *     
     *     return factoryBean.getObject();
     * }
     * }
     * </pre>
     */
    public static TenantSchemaInterceptor createSchemaInterceptor(TenantProperties tenantProperties) {
        return new TenantSchemaInterceptor(tenantProperties);
    }

    /**
     * 注册 Schema 拦截器到已存在的 SqlSessionFactory
     */
    public static void registerSchemaInterceptor(SqlSessionFactory sqlSessionFactory, 
                                                  TenantProperties tenantProperties) {
        if (tenantProperties.getIsolationLevel() == TenantIsolationLevel.SCHEMA) {
            TenantSchemaInterceptor schemaInterceptor = createSchemaInterceptor(tenantProperties);
            sqlSessionFactory.getConfiguration().addInterceptor(schemaInterceptor);
        }
    }
}
