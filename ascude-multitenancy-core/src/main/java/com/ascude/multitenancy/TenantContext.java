package com.ascude.multitenancy;


public class TenantContext {

    private static final ThreadLocal<Tenant> currentTenant = new InheritableThreadLocal<>();

    public static Tenant getCurrentTenant() {
        return currentTenant.get();
    }

    public static void setCurrentTenant(Tenant tenant) {
        currentTenant.set(tenant);
    }

    public static void clear() {
        currentTenant.set(null);
    }
}
