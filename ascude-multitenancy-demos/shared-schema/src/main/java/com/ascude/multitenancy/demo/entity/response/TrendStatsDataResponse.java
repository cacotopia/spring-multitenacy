package com.ascude.multitenancy.demo.entity.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TrendStatsDataResponse implements Serializable {

    // 日期标签
    private List<Integer> days;

    private List<Integer> visits;

}
