package com.ascude.multitenancy;

import java.util.Objects;

/**
 * 租户实体类
 * 包含租户的基本信息，支持树形结构的租户层级关系
 */
public class Tenant {

    /**
     * 租户唯一标识
     */
    private String id;

    private String code;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 租户域名（用于主机名方式解析租户）
     */
    private String domain;

    /**
     * 上级租户ID（用于构建租户树形结构）
     */
    private String parentId;

    /**
     * 租户状态（启用/禁用）
     */
    private boolean enabled = true;

    /**
     * 租户描述
     */
    private String description;

    // 构造方法
    public Tenant() {
    }

    public Tenant(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 重写equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return Objects.equals(id, tenant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", enabled=" + enabled +
                '}';
    }
    
}