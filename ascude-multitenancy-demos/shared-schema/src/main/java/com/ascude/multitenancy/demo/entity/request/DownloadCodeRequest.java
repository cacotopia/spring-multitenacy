package com.ascude.multitenancy.demo.entity.request;

import lombok.Data;

import java.util.List;

@Data
public class DownloadCodeRequest {

    private List<Long> ids;
}
