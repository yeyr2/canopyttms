package com.ttms.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ttms.enums.SeatStatus;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TokenAndT<T> {
    private T value;
    private String token;
    private String time;
    private Long uid;
    private SeatStatus status;
}
