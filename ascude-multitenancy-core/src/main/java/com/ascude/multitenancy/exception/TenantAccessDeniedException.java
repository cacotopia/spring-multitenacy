package com.ascude.multitenancy.exception;

/**
 * 租户访问拒绝异常
 * 当租户尝试访问不属于自己的资源时抛出
 */
public class TenantAccessDeniedException extends MultitenancyException {

    private final String requestedTenantId;
    private final String currentTenantId;

    public TenantAccessDeniedException(String requestedTenantId, String currentTenantId) {
        super(String.format("租户访问拒绝: 当前租户[%s]尝试访问租户[%s]的资源", 
            currentTenantId, requestedTenantId));
        this.requestedTenantId = requestedTenantId;
        this.currentTenantId = currentTenantId;
    }

    public String getRequestedTenantId() {
        return requestedTenantId;
    }

    public String getCurrentTenantId() {
        return currentTenantId;
    }
}
