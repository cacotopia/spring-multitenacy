package com.ascude.multitenancy.demo.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageListPermissionRequest extends BasePageRequest{

    private String type;

    private String permissionName;

    private String permissionCode;

    private Boolean sortByCreateDateAsc;

    private Boolean sortBySortAsc;

}
