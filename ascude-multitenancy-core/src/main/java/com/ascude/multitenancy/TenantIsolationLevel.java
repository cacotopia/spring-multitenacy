package com.ascude.multitenancy;

public enum TenantIsolationLevel {
    FIELD,    // 字段级隔离（默认）
    TABLE,    // 表级隔离
    SCHEMA,   // Schema级隔离
    DATABASE  // 数据库级隔离
}