package com.ascude.multitenancy.demo.util;

import lombok.Getter;

@Getter
public enum PermissionType {

    PAGE(1, "页面"),
    BUTTON(2, "按钮"),
    API(3, "API");



    private final Integer code;
    private final String desc;

    PermissionType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    // 根据code获取枚举实例的静态方法
    public static PermissionType getByCode(int code) {
        for (PermissionType status : PermissionType.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    // 重写toString方法
    @Override
    public String toString() {
        return "Status{" +
                "code=" + code +
                ", desc='" + desc +
                '}';
    }
}
