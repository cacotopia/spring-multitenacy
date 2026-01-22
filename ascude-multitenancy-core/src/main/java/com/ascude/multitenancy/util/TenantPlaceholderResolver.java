package com.ascude.multitenancy.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 租户占位符解析器
 * 用于解析包含 {id} 占位符的字符串模板
 */
public class TenantPlaceholderResolver {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{id}");

    private TenantPlaceholderResolver() {
        // 工具类不允许实例化
    }

    /**
     * 替换模板中的 {id} 占位符为实际的租户ID
     *
     * @param template 模板字符串，如 "tenant_{id}" 或 "t_{id}_"
     * @param tenantId 租户ID
     * @return 替换后的字符串
     */
    public static String resolve(String template, String tenantId) {
        if (StringUtils.isBlank(template)) {
            return "";
        }
        if (StringUtils.isBlank(tenantId)) {
            return template;
        }
        return PLACEHOLDER_PATTERN.matcher(template).replaceAll(Matcher.quoteReplacement(tenantId));
    }

    /**
     * 解析表名（支持前缀和后缀）
     *
     * @param tableName   原始表名
     * @param prefix      前缀模板，如 "t_{id}_"
     * @param suffix      后缀模板，如 "_t_{id}"
     * @param tenantId    租户ID
     * @return 解析后的表名
     */
    public static String resolveTableName(String tableName, String prefix, String suffix, String tenantId) {
        StringBuilder result = new StringBuilder();
        
        if (StringUtils.isNotBlank(prefix)) {
            result.append(resolve(prefix, tenantId));
        }
        
        result.append(tableName);
        
        if (StringUtils.isNotBlank(suffix)) {
            result.append(resolve(suffix, tenantId));
        }
        
        return result.toString();
    }

    /**
     * 解析Schema名称
     *
     * @param schemaPattern Schema模板，如 "tenant_{id}"
     * @param tenantId      租户ID
     * @return 解析后的Schema名称
     */
    public static String resolveSchemaName(String schemaPattern, String tenantId) {
        return resolve(schemaPattern, tenantId);
    }

    /**
     * 检查字符串是否包含占位符
     */
    public static boolean containsPlaceholder(String str) {
        return StringUtils.isNotBlank(str) && PLACEHOLDER_PATTERN.matcher(str).find();
    }
}
