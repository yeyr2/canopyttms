package com.ttms.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 总销售额
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinancialStatistics {
    private Double boxOfficeReceipts;  //票房收入
    private Integer numberOfScreenings; //放映数
    private Integer boxOfficeQuantity; //票房数量
    private Long Refunds; // 退票数
    private Integer numberViewers; //观众数量(票的使用数)
}
