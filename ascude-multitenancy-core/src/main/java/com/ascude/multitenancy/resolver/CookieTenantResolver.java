package com.ascude.multitenancy.resolver;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 从Cookie解析租户（默认键：tenant_id）
 */
public class CookieTenantResolver implements TenantResolver {

    private final String cookieName;

    public CookieTenantResolver(String cookieName) {
        this.cookieName = StringUtils.isNotBlank(cookieName) ? cookieName : "tenant_id";
    }

    @Override
    public String resolve(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public TenantSource getSource() {
        return TenantSource.COOKIE;
    }
}
