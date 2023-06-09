package com.ttms.pojo;

import lombok.Data;

/**
 *  演出计划收入
 */
@Data
public class VideoPlanSales {
    private Long id;
    private Long planId;
    private String video;
    private String Studio;
    private String time;
    private Integer numberViewers; //观众数量
    private Double sales; //销售额
}
