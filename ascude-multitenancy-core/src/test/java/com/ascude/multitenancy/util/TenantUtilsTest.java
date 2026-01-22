package com.ascude.multitenancy.util;

import com.ascude.multitenancy.TenantContext;
import com.ascude.multitenancy.exception.TenantNotFoundException;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TenantUtils 测试类
 */
public class TenantUtilsTest {

    @After
    public void cleanup() {
        TenantContext.clear();
    }

    @Test
    public void testGetCurrentTenantOrThrow_withTenant() {
        TenantContext.setCurrentTenant("tenant1");
        String tenantId = TenantUtils.getCurrentTenantOrThrow();
        assertEquals("tenant1", tenantId);
    }

    @Test(expected = TenantNotFoundException.class)
    public void testGetCurrentTenantOrThrow_withoutTenant() {
        TenantUtils.getCurrentTenantOrThrow();
    }

    @Test
    public void testGetCurrentTenantOrDefault() {
        TenantContext.setCurrentTenant("tenant1");
        assertEquals("tenant1", TenantUtils.getCurrentTenantOrDefault("default"));
        
        TenantContext.clear();
        assertEquals("default", TenantUtils.getCurrentTenantOrDefault("default"));
    }

    @Test
    public void testHasTenant() {
        assertFalse(TenantUtils.hasTenant());
        
        TenantContext.setCurrentTenant("tenant1");
        assertTrue(TenantUtils.hasTenant());
        
        TenantContext.clear();
        assertFalse(TenantUtils.hasTenant());
    }

    @Test
    public void testExecuteIgnoreTenant() {
        TenantContext.setCurrentTenant("tenant1");
        
        String result = TenantUtils.executeIgnoreTenant(() -> {
            assertNull(TenantContext.getCurrentTenant());
            return "executed";
        });
        
        assertEquals("executed", result);
        assertEquals("tenant1", TenantContext.getCurrentTenant());
    }

    @Test
    public void testExecuteWithTenant() {
        TenantContext.setCurrentTenant("tenant1");
        
        String result = TenantUtils.executeWithTenant("tenant2", () -> {
            assertEquals("tenant2", TenantContext.getCurrentTenant());
            return "executed";
        });
        
        assertEquals("executed", result);
        assertEquals("tenant1", TenantContext.getCurrentTenant());
    }
}
