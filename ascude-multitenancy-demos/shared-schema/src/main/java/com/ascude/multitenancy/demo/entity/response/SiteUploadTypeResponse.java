package com.ascude.multitenancy.demo.entity.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class SiteUploadTypeResponse implements Serializable {

    private String remarks;

    private String typeCode;
}
