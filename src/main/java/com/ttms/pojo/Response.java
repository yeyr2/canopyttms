package com.ttms.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    @JsonIgnore
    public final Integer SUCCESS = 0;
    @JsonIgnore
    public final Integer FAILURE = 1;
    private Integer Code;
    private String Msg;
    private Object value;
    private String token;
    public Response() {
        Code = SUCCESS;
    }

    public Response(String msg) {
        Code = FAILURE;
        Msg = msg;
    }

    public Response(Integer code, String msg, Object object) {
        Code = code == 1 ? FAILURE : SUCCESS;
        Msg = msg;
        this.value = object;
    }

    public Integer getCode() {
        return Code;
    }

    public void setCode(Integer code) {
        Code = code;
    }

    public String getMsg() {
        return Msg;
    }

    public Response setMsg(String msg) {
        Msg = msg;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public Response setValue(Object object) {
        this.value = object;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Response setToken(String token) {
        this.token = token;
        return this;
    }
}
