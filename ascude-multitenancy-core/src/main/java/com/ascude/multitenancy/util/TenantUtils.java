package com.ascude.multitenancy.util;

import com.ascude.multitenancy.Tenant;
import com.ascude.multitenancy.TenantContext;
import com.ascude.multitenancy.exception.TenantNotFoundException;

/**
 * 租户工具类
 */
public class TenantUtils {

    private TenantUtils() {
        // 工具类不允许实例化
    }

    /**
     * 获取当前租户ID，如果不存在则抛出异常
     */
    public static Tenant getCurrentTenantOrThrow() {
        Tenant tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw TenantNotFoundException.noTenantInContext();
        }
        return tenantId;
    }

    /**
     * 获取当前租户ID，如果不存在则返回默认值
     */
    public static String getCurrentTenantOrDefault(String defaultTenant) {
        Tenant tenantId = TenantContext.getCurrentTenant();
        return tenantId != null ? tenantId.getId() : defaultTenant;
    }

    /**
     * 检查是否有租户上下文
     */
    public static boolean hasTenant() {
        return TenantContext.getCurrentTenant() != null;
    }

    /**
     * 执行忽略租户上下文的操作
     */
    public static <T> T executeIgnoreTenant(TenantCallback<T> callback) {
        Tenant originalTenant = TenantContext.getCurrentTenant();
        try {
            TenantContext.clear();
            return callback.execute();
        } finally {
            if (originalTenant != null) {
                TenantContext.setCurrentTenant(originalTenant);
            }
        }
    }

    /**
     * 使用指定租户执行操作
     */
    public static <T> T executeWithTenant(String tenantId, TenantCallback<T> callback) {
        Tenant originalTenant = TenantContext.getCurrentTenant();
        try {
            TenantContext.setCurrentTenant(new Tenant(tenantId));
            return callback.execute();
        } finally {
            if (originalTenant != null) {
                TenantContext.setCurrentTenant(originalTenant);
            } else {
                TenantContext.clear();
            }
        }
    }



    /**
     * 租户回调接口
     */
    @FunctionalInterface
    public interface TenantCallback<T> {
        T execute();
    }
}
