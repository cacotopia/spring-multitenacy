package com.ascude.multitenancy.exception;

/**
 * 租户解析异常
 * 当租户解析过程中发生错误时抛出
 */
public class TenantResolveException extends MultitenancyException {

    public TenantResolveException(String message) {
        super(message);
    }

    public TenantResolveException(String message, Throwable cause) {
        super(message, cause);
    }
}
