package com.ttms.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ttms.enums.TicketStatus;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ticket {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    @TableField(value = "order_id")
    private Long oid;
    private Integer ticketRows;
    private Integer ticketColumns;
    /**
     * @0:已售,1:未售,2:已过期,3:已退票
     */
    private TicketStatus status;
    private Long lastTime;

    public Ticket() {}

    public Ticket(Long planId, Integer seatRows, Integer seatColumns, TicketStatus status) {
        this.planId = planId;
        this.ticketRows = seatRows;
        this.ticketColumns = seatColumns;
        this.status = status;
    }

}
