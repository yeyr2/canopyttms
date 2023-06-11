package com.ttms.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 *  演出计划收入
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoPlanSales {
    private List<Long> planId;
    private String video;
    private String time;
    private Integer ticketNumber; // 票的数量
    private Long Refunds; // 退票数
    private Integer numberViewers; //观众数量
    private Double sales; //销售额
}
