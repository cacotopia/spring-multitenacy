package com.ascude.multitenancy.demo.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PageListUploadBaseInfoRequest extends BasePageRequest {

    private String type;

    private Boolean sortByCreateDateAsc;

    
}
