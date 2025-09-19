package com.ascude.multitenancy.resolver;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 从请求头解析租户（默认键：X-Tenant-Id）
 */
public class HeaderTenantResolver implements TenantResolver {

    private final String headerName;

    // 可通过配置指定请求头键名，默认X-Tenant-Id
    public HeaderTenantResolver(String headerName) {
        this.headerName = StringUtils.isNoneBlank(headerName) ? headerName : "X-Tenant-Id";
    }

    @Override
    public String resolve(HttpServletRequest request) {
        return request.getHeader(headerName);
    }

    @Override
    public TenantSource getSource() {
        return TenantSource.HEADER;
    }

}
