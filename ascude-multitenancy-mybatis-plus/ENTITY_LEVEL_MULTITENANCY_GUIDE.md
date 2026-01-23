# Entity-Level Multi-Tenancy Strategy Guide

## Overview

This implementation supports **mixed multi-tenancy strategies** in a single application, where different entities can use different isolation levels based on the `@Multitenant` annotation.

## Supported Isolation Levels

- **FIELD**: Field-level isolation (discriminator column like `tenant_id`)
- **TABLE**: Table-level isolation (separate tables per tenant like `user_t1`, `user_t2`)
- **SCHEMA**: Schema-level isolation (separate database schemas)
- **DATABASE**: Database-level isolation (completely separate databases)

## Configuration

### 1. Add Dependency

```xml
<dependency>
    <groupId>com.ascude.multitenancy</groupId>
    <artifactId>ascude-multitenancy-starter-mybatis-plus</artifactId>
    <version>1.0.01-SNAPSHOT</version>
</dependency>
```

### 2. Application Configuration

```yaml
spring:
  multitenancy:
    # Entity scanning packages (required)
    entity-scan-packages:
      - com.example.entity
      - com.example.module.*.entity
    
    # Default isolation level (if entity has no @Multitenant annotation)
    default-level: FIELD
    
    # Default tenant ID field name
    default-field-name: tenant_id
    
    # For TABLE level: table name pattern
    table-prefix: ""
    table-suffix: "_t_{id}"
    
    # For SCHEMA level: schema pattern
    global-schema-pattern: tenant_{id}
    
    # For DATABASE level: database URL pattern
    global-database-url-pattern: jdbc:mysql://localhost:3306/tenant_{id}
```

## Usage Examples

### Example 1: Mixed Strategies in One Application

```java
// User entity: Field-level isolation (shared table with tenant_id column)
@TableName("sys_user")
@Multitenant(level = TenantIsolationLevel.FIELD, fieldName = "tenant_id")
public class User {
    private Long id;
    private String username;
    private String tenantId;  // Automatically filtered by interceptor
}

// Order entity: Table-level isolation (separate tables per tenant)
@TableName("biz_order")
@Multitenant(
    level = TenantIsolationLevel.TABLE,
    tablePrefix = "t_{id}_",
    tableSuffix = ""
)
public class Order {
    private Long id;
    private String orderNo;
    // No tenant_id field needed - table name becomes "t_tenant1_biz_order"
}

// Product entity: Schema-level isolation (separate schemas)
@TableName("product")
@Multitenant(
    level = TenantIsolationLevel.SCHEMA,
    schemaPattern = "tenant_{id}"
)
public class Product {
    private Long id;
    private String productName;
    // Accessed from schema "tenant_tenant1.product"
}

// System configuration: Ignore multi-tenancy
@TableName("sys_config")
@Multitenant(ignore = true)
public class SysConfig {
    private Long id;
    private String configKey;
    // This table is never filtered - shared across all tenants
}
```

### Example 2: Dynamic Configuration

You can also configure table prefixes/suffixes dynamically through `TenantConfigService`:

```java
@Service
public class CustomTenantConfigService implements TenantConfigService {
    
    @Override
    public TenantTableConfig getTableConfig(String tenantId, String entityClassName) {
        // Load from database or cache
        TenantTableConfig config = new TenantTableConfig();
        
        if ("com.example.entity.Order".equals(entityClassName)) {
            config.setTablePrefix("tenant_" + tenantId + "_");
            config.setTableSuffix("");
        }
        
        return config;
    }
    
    @Override
    public TenantTableConfig getDefaultTableConfig(String tenantId) {
        TenantTableConfig config = new TenantTableConfig();
        config.setTablePrefix("");
        config.setTableSuffix("_t_" + tenantId);
        return config;
    }
}
```

## How It Works

### 1. Entity Scanning

At application startup, the `EntityScanner` automatically scans all entities in configured packages and registers them with `EntityCache`. This builds a mapping between table names and entity classes.

### 2. Interceptor Routing

When a SQL query is executed:

1. **TenantLineHandler** (Field-level): 
   - Checks if the entity's isolation level is `FIELD`
   - If yes, automatically adds `WHERE tenant_id = ?` to the query
   - If no, ignores this table

2. **TableNameHandler** (Table-level):
   - Checks if the entity's isolation level is `TABLE`
   - If yes, rewrites table name (e.g., `order` → `t_tenant1_order`)
   - If no, keeps original table name

3. **SchemaInterceptor** (Schema-level):
   - Checks if any entity uses `SCHEMA` isolation
   - If yes, switches database schema before query execution
   - Uses `SET search_path` (PostgreSQL) or `USE` (MySQL)

### 3. Table Name to Entity Mapping

The `EntityCache` maintains a bidirectional mapping:
- Entity Class → Table Name (via `@TableName` annotation or naming convention)
- Table Name → Entity Class → Tenant Config

When an interceptor encounters a table name in SQL, it:
1. Looks up the entity class by table name
2. Retrieves the `@Multitenant` configuration
3. Applies the appropriate isolation strategy

## Best Practices

### 1. Choose Appropriate Strategy

- **FIELD**: Best for most tables, easy to manage, good performance
- **TABLE**: Use for high-volume tables that need complete separation
- **SCHEMA**: Use when tenant data must be logically separated (compliance)
- **DATABASE**: Use for complete tenant isolation (security, performance)

### 2. Entity Scanning Performance

- Be specific with scan packages to avoid unnecessary scanning
- Use exclude filters if needed:

```yaml
spring:
  multitenancy:
    entity-scan-packages:
      - com.example.entity      # Scan this package
      - com.example.order.*     # Scan sub-packages
```

### 3. Handling System Tables

Always mark system/configuration tables as ignored:

```java
@Multitenant(ignore = true)
public class SysConfig { ... }
```

### 4. Testing

Create test entities with different strategies:

```java
@SpringBootTest
class MultiTenancyTest {
    
    @Autowired
    private UserMapper userMapper;  // FIELD level
    
    @Autowired
    private OrderMapper orderMapper;  // TABLE level
    
    @Test
    void testMixedStrategy() {
        TenantContext.setCurrentTenant(new Tenant("tenant1"));
        
        // User query: SELECT * FROM sys_user WHERE tenant_id = 'tenant1'
        List<User> users = userMapper.selectList(null);
        
        // Order query: SELECT * FROM t_tenant1_biz_order
        List<Order> orders = orderMapper.selectList(null);
        
        TenantContext.clear();
    }
}
```

## Troubleshooting

### Issue 1: Entity Not Recognized

**Symptom**: Table not being filtered even with `@Multitenant` annotation

**Solution**: 
- Check if the entity's package is in `entity-scan-packages`
- Verify entity has `@TableName` annotation
- Check logs for "Registered entity" messages

### Issue 2: Wrong Table Name Resolution

**Symptom**: Table name not matching expected pattern

**Solution**:
- Ensure `@TableName` value matches database table name exactly
- Check if `tablePrefix`/`tableSuffix` patterns are correct
- Use `{id}` placeholder for tenant ID replacement

### Issue 3: Schema Switching Not Working

**Symptom**: Still accessing default schema

**Solution**:
- Verify database driver supports `setSchema()` or `SET search_path`
- Check database user has permission to access tenant schemas
- Enable debug logging: `logging.level.com.ascude.multitenancy=DEBUG`

## Migration Guide

### From Global Configuration to Entity-Level

**Before** (global configuration):
```yaml
tenant:
  isolation-level: FIELD
  tenant-id-column: tenant_id
```

**After** (entity-level):
```java
@Multitenant(level = TenantIsolationLevel.FIELD, fieldName = "tenant_id")
public class User { ... }

@Multitenant(level = TenantIsolationLevel.TABLE, tableSuffix = "_t_{id}")
public class Order { ... }
```

The global configuration now serves as a **default** for entities without `@Multitenant`.

## Performance Considerations

1. **Entity Scanning**: Happens once at startup, minimal overhead
2. **Table Lookup**: O(1) HashMap lookup per query, negligible impact
3. **Interceptor Chain**: All three interceptors are active, but each checks isolation level and shortcuts if not applicable
4. **Recommendation**: Use FIELD level for most tables to minimize interceptor overhead

## Advanced: Custom Entity Resolution

If you need custom table-to-entity mapping logic:

```java
@Configuration
public class CustomEntityCacheConfig {
    
    @Bean
    public EntityCache customEntityCache(MultitenantConfig config, TenantConfigService service) {
        EntityCache cache = new EntityCache(config, service);
        
        // Manually register entities with custom table names
        cache.registerEntity(LegacyUser.class);
        cache.registerEntity(ExternalProduct.class);
        
        return cache;
    }
}
```

## Summary

This entity-level multi-tenancy implementation provides:
- ✅ Flexible per-entity isolation strategy configuration
- ✅ Mixed strategies in one application
- ✅ Automatic entity scanning and registration
- ✅ Zero code changes for query logic
- ✅ Backward compatible with global configuration
- ✅ Easy to test and maintain

For questions or issues, please refer to the project documentation or submit an issue.
