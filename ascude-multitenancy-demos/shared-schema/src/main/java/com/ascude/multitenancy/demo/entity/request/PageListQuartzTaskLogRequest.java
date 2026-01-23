package com.ascude.multitenancy.demo.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageListQuartzTaskLogRequest extends BasePageRequest{

    private String name;

    private Boolean sortByCreateDateAsc;
}
