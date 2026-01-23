package com.ascude.multitenancy.demo.entity.response;

import lombok.Data;

import java.util.Set;

@Data
public class UserDetailResponse {

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

}
