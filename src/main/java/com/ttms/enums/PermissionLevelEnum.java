package com.ttms.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;


public enum PermissionLevelEnum {
    audience(0,"观众"),
    admin(1,"管理员"),
    conductor(2,"售票员"),
    financialOfficer(3,"财务员"),
    ticketInspector(4,"验票员"),
    Clerk(5,"业务员");

    @EnumValue
    private final int id;
    private final String level;

    PermissionLevelEnum(int id, String level) {
        this.id = id;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public String getLevel() {
        return level;
    }

    public boolean verify() {
        return this.getId() >= 0 && this.getId() <= 5;
    }
}
