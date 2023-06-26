package com.ttms.component;

import com.ttms.pojo.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseEntityComponent {
    public static final ResponseEntity<Response> Exceeds_Maximum =
            new ResponseEntity<>(new Response("err:Page count exceeds maximum"), HttpStatus.OK);
    public static final ResponseEntity<Response> User_Not_Exist =
            new ResponseEntity<>(new Response("err:User don't exist"), HttpStatus.OK);
    public static final ResponseEntity<Response> User_Exist =
            new ResponseEntity<>(new Response("err:User already exists"), HttpStatus.OK);
    public static final ResponseEntity<Response> Token_Err =
            new ResponseEntity<>(new Response("err: token err,non-existent token."),HttpStatus.OK);
    public static final ResponseEntity<Response> PROPERTIES_ERR =
            new ResponseEntity<>(new Response("err : 传入参数有误。"),HttpStatus.OK);

    public static final ResponseEntity<Response> ID_ERR =
            new ResponseEntity<>(new Response("err : 没有id."),HttpStatus.OK);

    public static final ResponseEntity<Response> USER_CHANGE_ERR =
            new ResponseEntity<>(new Response("err : 所有参数与之前相同。"),HttpStatus.OK);

    public static final ResponseEntity<Response> Seat_Fixed =
            new ResponseEntity<>(new Response("err : 座位对应的票已被售出。"),HttpStatus.OK);

    public static ResponseEntity<Response> Password_Err(String str) {
        return new ResponseEntity<>(new Response("密码错误："+str),HttpStatus.OK);
    }

    public static ResponseEntity<Response> verificationCode_Err() {
        return new ResponseEntity<>(new Response("err : 验证码错误"),HttpStatus.OK);
    }

    public static ResponseEntity<Response> Expired(String name) {
        return new ResponseEntity<>(new Response("err : "+name+"失去了有效性."),HttpStatus.OK);
    }

    public static ResponseEntity<Response> Sole_Err(Integer row,Integer column) {
        return new ResponseEntity<>(new Response("err : "+row+"行"+column+"列"+"已被售出."),HttpStatus.OK);
    }

    public static ResponseEntity<Response> Studio_Err(String studio) {
        return new ResponseEntity<>(new Response("err : 没有该演出厅："+studio+"."),HttpStatus.OK);
    }

    public static ResponseEntity<Response> Conflict(String type) {
        String context;
        if ("name".equals(type)) {
            context = type+"已经存在";
        }else{
            context = type+"不存在";
        }
        return new ResponseEntity<>(new Response("err : 产生冲突，可能是"+context),HttpStatus.OK);
    }

    public static ResponseEntity<Response> Create_Failed(String type) {
        return new ResponseEntity<>(new Response("err:Create "+type+" failed."), HttpStatus.OK);
    }

    public static ResponseEntity<Response> Update_Failed(String type) {
        return new ResponseEntity<>(new Response("err:Update "+type+" failed."), HttpStatus.OK);
    }

    public static ResponseEntity<Response> Delete_Failed(String type) {
        return new ResponseEntity<>(new Response("err:Delete "+type+" failed."), HttpStatus.OK);
    }

    public static ResponseEntity<Response> Wrong_Format(String type) {
        return new ResponseEntity<>(new Response("err: "+type+" format error."),HttpStatus.OK);
    }

    public static ResponseEntity<Response> Not_Found(String s) {
        return new ResponseEntity<>(new Response("err: not found "+s),HttpStatus.OK);
    }

    public static class ResponseController {

    }
}


