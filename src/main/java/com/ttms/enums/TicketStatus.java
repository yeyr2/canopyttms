package com.ttms.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum TicketStatus {
    Sold(0,"已售"),
    Unsold(1,"未售"),
    Locking(2,"锁定"),
    Expired(3,"已过期"),
    None(4,"没有该位置"),
    Used(5,"已使用"),
    ExpiredSole(6,"已购但过期");

    @EnumValue
    private Integer id;
    private String value;

    TicketStatus(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
