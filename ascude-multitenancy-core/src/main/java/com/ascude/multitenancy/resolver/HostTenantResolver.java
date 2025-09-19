package com.ascude.multitenancy.resolver;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 从主机名解析租户（如 tenant1.example.com → tenant1）
 */
public class HostTenantResolver implements TenantResolver {
    // 基础域名（如 example.com），用于截取租户前缀
    private final String baseDomain;

    public HostTenantResolver(String baseDomain) {
        this.baseDomain = baseDomain;
    }

    @Override
    public String resolve(HttpServletRequest request) {
        String host = request.getServerName(); // 如 tenant1.example.com
        if (!host.endsWith(baseDomain)) {
            return null; // 不匹配基础域名则无法解析
        }
        // 截取租户标识（tenant1.example.com → tenant1）
        String tenantPart = host.substring(0, host.length() - baseDomain.length() - 1);
        return StringUtils.isNoneBlank(tenantPart) ? tenantPart : null;
    }

    @Override
    public TenantSource getSource() {
        return TenantSource.HOST;
    }
}
