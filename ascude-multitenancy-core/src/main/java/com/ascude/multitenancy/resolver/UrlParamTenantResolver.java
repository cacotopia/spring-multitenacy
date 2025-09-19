package com.ascude.multitenancy.resolver;


import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 从URL参数解析租户（默认键：tenantId）
 */
public class UrlParamTenantResolver implements TenantResolver {

    private final String paramName;

    public UrlParamTenantResolver(String paramName) {
        this.paramName = StringUtils.isNoneBlank(paramName) ? paramName : "tenantId";
    }

    @Override
    public String resolve(HttpServletRequest request) {
        return request.getParameter(paramName);
    }

    @Override
    public TenantSource getSource() {
        return TenantSource.URL_PARAM;
    }

}