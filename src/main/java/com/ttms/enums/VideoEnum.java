package com.ttms.enums;

public enum VideoEnum {
    Default(1,"Default"),
    Score(2,"Score"),
    Time(3,"Time"),
    Type(4,"Type"),
    Hot(5,"Hot"),
    ComingSoon(6,"ComingSoon"),
    Name(7,"Name");

    final int id;
    final String type;
    VideoEnum(int id, String type) {
        this.type = type;
        this.id = id;
    }
}
