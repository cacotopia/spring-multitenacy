package com.ascude.multitenancy.exception;

/**
 * 租户未找到异常
 * 当无法从请求中解析出租户信息时抛出
 */
public class TenantNotFoundException extends MultitenancyException {

    public TenantNotFoundException(String message) {
        super(message);
    }

    public TenantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static TenantNotFoundException noTenantInContext() {
        return new TenantNotFoundException("当前请求上下文中未设置租户信息");
    }

    public static TenantNotFoundException cannotResolveTenant() {
        return new TenantNotFoundException("无法从请求中解析租户信息");
    }
}
