package com.ttms.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ttms.enums.OrderStatus;
import lombok.Data;

import java.util.List;

@Data
@TableName("t_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long uid;
    private String time;
    //0:未支付 ， 1：已支付 ， 2：取消订单 ， 3：过期
    private OrderStatus orderStatus;
    private Long studioId;
    private String studio;
    private Long videoId;
    private String video;
    private Double price;
    private Long planId;
    @TableField(exist = false)
    private List<Ticket> tickets;
}
