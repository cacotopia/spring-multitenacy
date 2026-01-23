package com.ascude.multitenancy.demo.entity.response;

import lombok.Data;

import java.util.Set;

@Data
public class CurrentUserResponse {

    private Long id;

    private String loginName;

    private String nickName;

    private String icon;

    private String email;

    private String tel;

    private String remarks;

    /**
     * 位置信息
     */
    private String location;

    /**
     * 上次登录IP
     */
    private String lastLoginIp;

    /**
     * 上次登录时间
     */
    private String lastLoginTime;

    /**
     * 上次登录地址
     */
    public String lastLoginLocation;
}
