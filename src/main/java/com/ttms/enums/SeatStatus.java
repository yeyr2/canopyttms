package com.ttms.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum SeatStatus {
    UnderMaintenance(0,"正在维修"),
    None(1,"空"),
    Normal(2,"正常");

    @EnumValue //将注解所标识的属性的值存储到数据库中
    private Integer id;
    private String value;

    SeatStatus(){}
    SeatStatus(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
