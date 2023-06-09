package com.ttms.pojo;

import lombok.Data;

import java.util.List;

/**
 *  演出计划收入
 */
@Data
public class VideoPlanSales {
    private List<Long> planId;
    private String video;
    private String time;
    private Long Refunds; // 退票数
    private Integer numberViewers; //观众数量
    private Double sales; //销售额
}
