package com.ascude.multitenancy.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TenantPlaceholderResolver 测试类
 */
public class TenantPlaceholderResolverTest {

    @Test
    public void testResolve() {
        assertEquals("tenant_001", 
            TenantPlaceholderResolver.resolve("tenant_{id}", "001"));
        
        assertEquals("", 
            TenantPlaceholderResolver.resolve("", "001"));
        
        assertEquals("tenant_{id}", 
            TenantPlaceholderResolver.resolve("tenant_{id}", ""));
    }

    @Test
    public void testResolveTableName() {
        assertEquals("t_001_user", 
            TenantPlaceholderResolver.resolveTableName("user", "t_{id}_", "", "001"));
        
        assertEquals("user_t_001", 
            TenantPlaceholderResolver.resolveTableName("user", "", "_t_{id}", "001"));
        
        assertEquals("t_001_user_t_001", 
            TenantPlaceholderResolver.resolveTableName("user", "t_{id}_", "_t_{id}", "001"));
        
        assertEquals("user", 
            TenantPlaceholderResolver.resolveTableName("user", "", "", "001"));
    }

    @Test
    public void testResolveSchemaName() {
        assertEquals("tenant_001", 
            TenantPlaceholderResolver.resolveSchemaName("tenant_{id}", "001"));
        
        assertEquals("schema_abc", 
            TenantPlaceholderResolver.resolveSchemaName("schema_{id}", "abc"));
    }

    @Test
    public void testContainsPlaceholder() {
        assertTrue(TenantPlaceholderResolver.containsPlaceholder("tenant_{id}"));
        assertTrue(TenantPlaceholderResolver.containsPlaceholder("prefix_{id}_suffix"));
        assertFalse(TenantPlaceholderResolver.containsPlaceholder("tenant_001"));
        assertFalse(TenantPlaceholderResolver.containsPlaceholder(""));
        assertFalse(TenantPlaceholderResolver.containsPlaceholder(null));
    }

    @Test
    public void testMultiplePlaceholders() {
        assertEquals("tenant_001_schema_001", 
            TenantPlaceholderResolver.resolve("tenant_{id}_schema_{id}", "001"));
    }
}
