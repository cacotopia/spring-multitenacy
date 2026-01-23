package com.ascude.multitenancy.mybatis;

import com.ascude.multitenancy.Tenant;
import com.ascude.multitenancy.TenantContext;
import com.ascude.multitenancy.TenantIsolationLevel;
import com.ascude.multitenancy.mybatisplus.annotations.Multitenant;
import com.ascude.multitenancy.mybatisplus.TenantTableConfig;
import com.ascude.multitenancy.mybatisplus.service.TenantConfigService;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类多租户配置缓存
 * 负责扫描和缓存所有实体的多租户配置信息，并提供表名到实体的映射
 */
public class EntityCache {

    private static final Logger logger = LoggerFactory.getLogger(EntityCache.class);

    private MultitenantConfig multitenantConfig;

    private TenantConfigService tenantConfigService;

    public EntityCache(MultitenantConfig config, TenantConfigService configService) {
        this.multitenantConfig = config;
        this.tenantConfigService = configService;
    }

    private final ConcurrentHashMap<Class<?>, EntityTenantInfo> entityInfoCache = new ConcurrentHashMap<>();
    
    /**
     * 表名到实体类的映射缓存（用于根据表名查找实体配置）
     */
    private final ConcurrentHashMap<String, Class<?>> tableNameToEntityCache = new ConcurrentHashMap<>();

    /**
     * 获取实体类的多租户配置信息
     */
    public EntityTenantInfo getEntityTenantInfo(Class<?> entityClass) {
        return entityInfoCache.computeIfAbsent(entityClass,  key -> buildEntityTenantInfo(key));
    }

    /**
     * 获取实体类的多租户配置信息（包含动态表配置）
     * 当使用表级隔离时，会从租户服务获取动态配置
     */
    public EntityTenantInfo getEntityTenantInfoWithDynamicConfig(Class<?> entityClass) {
        // 获取基础配置
        EntityTenantInfo info = getEntityTenantInfo(entityClass);

        // 如果是表级隔离且当前有租户上下文，尝试获取动态配置
        if (info.getLevel() == TenantIsolationLevel.TABLE && TenantContext.getCurrentTenant() != null) {
            Tenant tenantCurrent = TenantContext.getCurrentTenant();
            String entityClassName = entityClass.getName();

            // 尝试获取实体类特定的配置
            TenantTableConfig dynamicConfig = tenantConfigService.getTableConfig(tenantCurrent.getId(), entityClassName);

            // 如果没有实体类特定的配置，尝试获取默认配置
            if (dynamicConfig == null) {
                dynamicConfig = tenantConfigService.getDefaultTableConfig(tenantCurrent.getId());
            }

            // 如果有动态配置，创建一个新的配置实例（避免修改缓存的基础配置）
            if (dynamicConfig != null) {
                EntityTenantInfo dynamicInfo = new EntityTenantInfo();
                dynamicInfo.setLevel(info.getLevel());
                dynamicInfo.setFieldName(info.getFieldName());
                dynamicInfo.setTablePrefix(dynamicConfig.getTablePrefix());
                dynamicInfo.setTableSuffix(dynamicConfig.getTableSuffix());
                dynamicInfo.setSchemaPattern(info.getSchemaPattern());
                dynamicInfo.setIgnore(info.isIgnore());
                return dynamicInfo;
            }
        }

        // 返回基础配置或合并后的配置
        return info;
    }

    private EntityTenantInfo buildEntityTenantInfo(Class<?> entityClass) {
        Multitenant multitenant = entityClass.getAnnotation(Multitenant.class);
        EntityTenantInfo info = new EntityTenantInfo();

        if (multitenant != null) {
            info.setLevel(multitenant.level());
            info.setFieldName(multitenant.fieldName());
            info.setTablePrefix(multitenant.tablePrefix());
            info.setTableSuffix(multitenant.tableSuffix());
            info.setSchemaPattern(multitenant.schemaPattern());
            info.setIgnore(multitenant.ignore());
        } else {
            // 使用全局默认配置
            info.setLevel(multitenantConfig.getDefaultLevel());
            info.setFieldName(multitenantConfig.getDefaultFieldName());
            info.setIgnore(false);
        }

        // Schema和Database级别使用全局模式
        if (info.getLevel() == TenantIsolationLevel.SCHEMA && info.getSchemaPattern().isEmpty()) {
            info.setSchemaPattern(multitenantConfig.getGlobalSchemaPattern());
        }

        return info;
    }

    /**
     * 注册实体类（在应用启动时扫描并注册所有实体）
     * 建立表名与实体类的映射关系
     */
    public void registerEntity(Class<?> entityClass) {
        // 获取实体的多租户配置
        EntityTenantInfo tenantInfo = getEntityTenantInfo(entityClass);
        
        // 获取表名
        String tableName = getTableName(entityClass);
        if (StringUtils.isNotBlank(tableName)) {
            tableNameToEntityCache.put(tableName.toLowerCase(), entityClass);
            logger.debug("Registered entity: {} -> table: {} with isolation level: {}", 
                    entityClass.getSimpleName(), tableName, tenantInfo.getLevel());
        }
    }
    
    /**
     * 根据表名获取实体类
     */
    public Class<?> getEntityByTableName(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        return tableNameToEntityCache.get(tableName.toLowerCase());
    }
    
    /**
     * 根据表名获取多租户配置信息
     */
    public EntityTenantInfo getEntityTenantInfoByTableName(String tableName) {
        Class<?> entityClass = getEntityByTableName(tableName);
        if (entityClass != null) {
            return getEntityTenantInfo(entityClass);
        }
        // 如果找不到对应实体，返回全局默认配置
        EntityTenantInfo defaultInfo = new EntityTenantInfo();
        defaultInfo.setLevel(multitenantConfig.getDefaultLevel());
        defaultInfo.setFieldName(multitenantConfig.getDefaultFieldName());
        defaultInfo.setIgnore(false);
        return defaultInfo;
    }
    
    /**
     * 获取实体类对应的表名
     */
    private String getTableName(Class<?> entityClass) {
        // 优先从 @TableName 注解获取
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        if (tableNameAnnotation != null && StringUtils.isNotBlank(tableNameAnnotation.value())) {
            return tableNameAnnotation.value();
        }
        
        // 降级使用驼峰转下划线命名规则
        return camelToUnderscore(entityClass.getSimpleName());
    }
    
    /**
     * 驼峰命名转下划线
     */
    private String camelToUnderscore(String camelCase) {
        if (StringUtils.isBlank(camelCase)) {
            return camelCase;
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(camelCase.charAt(0)));
        for (int i = 1; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
    
    /**
     * 提供方法来更新实体租户信息缓存
     */
    public void updateEntityTenantInfo(Class<?> entityClass, EntityTenantInfo tenantInfo) {
        entityInfoCache.put(entityClass, tenantInfo);
    }
    
    /**
     * 获取所有已注册的实体类
     */
    public ConcurrentHashMap<String, Class<?>> getAllRegisteredEntities() {
        return tableNameToEntityCache;
    }

}