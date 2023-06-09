package com.ttms.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Manage<T> {
    private List<T> values;
    private Integer rows;
    private Integer columns;
}
