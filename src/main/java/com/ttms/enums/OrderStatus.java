package com.ttms.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum OrderStatus {
    //0:未支付 ， 1：已支付 ， 2：取消订单 ， 3：过期
    unSole(0,"未支付"),
    Paid(1,"已支付"),
    CancelOrder(2,"取消订单"),
    Overdue(3,"过期"),
    Used(4,"已使用");

    @EnumValue
    int id;
    String value;

    OrderStatus(int id,String value){
        this.id = id;
        this.value = value;
    }
}
