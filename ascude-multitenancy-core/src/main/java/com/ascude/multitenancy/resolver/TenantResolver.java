package com.ascude.multitenancy.resolver;

import javax.servlet.http.HttpServletRequest;

/**
 * 租户解析器接口：从不同来源解析租户标识
 */
public interface TenantResolver {

    /**
     * 从请求中解析租户标识（如租户ID或租户唯一键）
     */
    String resolve(HttpServletRequest request);

    /**
     * 获取解析来源
     */
    TenantSource getSource();

    /**
     * 租户来源枚举
     */
    enum TenantSource {

        HEADER("Header"),      // 请求头
        COOKIE("Cookie"),      // Cookie
        HOST("Host"),          // 主机名
        URL_PARAM("URL Param");  // URL参数

        private final String desc;

        TenantSource(String desc) {
            this.desc = desc;
        }
    }

}

