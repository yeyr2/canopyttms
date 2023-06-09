package com.ttms.pojo;

import lombok.Data;

/**
 * 总销售额
 */
@Data
public class FinancialStatistics {
    private Double boxOfficeReceipts;  //票房收入
    private Integer numberOfScreenings; //放映数
    private Integer boxOfficeQuantity; //票房数量
    private Integer Refunds; // 退票数
    private Integer numberViewers; //观众数量(票的使用数)
    private Integer salesVolume; //销售量
}
