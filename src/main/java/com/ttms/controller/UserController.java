package com.ttms.controller;

import com.ttms.component.ResponseEntityComponent;
import com.ttms.component.UserComponent;
import com.ttms.enums.PermissionLevelEnum;
import com.ttms.pojo.Response;
import com.ttms.pojo.TokenAndT;
import com.ttms.pojo.User;
import com.ttms.tools.TokenUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserComponent userComponent;
    /**
     *
     * @param user  用户信息
     */
    @PostMapping(value = "/login")
    public ResponseEntity<Response> Login(@RequestBody User user) {

        return userComponent.login(user);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Response> Register(@RequestBody User user) {

        return userComponent.register(user);
    }

    @GetMapping("/userInfo")
    public ResponseEntity<Response> getUserInfo(@RequestParam("token") String token, @RequestParam(value = "id") Long id) {
        if (TokenUtils.NoLevelVerify(token) == null) {
            return ResponseEntityComponent.Token_Err;
        }
        Long uid = TokenUtils.decodeGetIdByToken(token);
        if(!uid.equals(id)){
            return ResponseEntityComponent.ID_ERR;
        }

        return userComponent.getUserInfo(id);
    }

    @GetMapping("/usersInfo")
    public ResponseEntity<Response> getUsersInfo(@RequestParam("token") String token, @RequestParam(value = "ids",required = false) List<Long> ids,@RequestParam(value = "isAdmin",required = false) Boolean isAdmin) {
        if (TokenUtils.verify(token,true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        return userComponent.getUsersInfo(ids,isAdmin);
    }

    @GetMapping("/getUserByName")
    public ResponseEntity<Response> getUserByName(@RequestParam("token") String token,@RequestParam("name") String name,@RequestParam(value = "isAdmin",required = false) Boolean isAdmin) {
        if (TokenUtils.verify(token,true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        return userComponent.getUserByName(name,isAdmin);
    }

    @PostMapping("/changeUserInfo")
    public ResponseEntity<Response> changeUserInfo(@RequestBody TokenAndT<User> tokenAndUser) {
        boolean flag = false;
        String token = tokenAndUser.getToken();
        if((TokenUtils.verify(token,true) != null) ||
                (TokenUtils.verify(token,false) != null && TokenUtils.decodeGetIdByToken(token).equals(tokenAndUser.getValue().getId()))){
            flag = true;
        }

        if (!flag){
            return ResponseEntityComponent.Token_Err;
        }

        User user = tokenAndUser.getValue();

        return userComponent.changeUserInfo(user);
    }

    @PostMapping("/changeUserLevel")
    public ResponseEntity<Response> changeUserLevel(@RequestBody ChangeUserLevel changeUserLevel) {
        if (TokenUtils.verify(changeUserLevel.getToken(),true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        PermissionLevelEnum level = changeUserLevel.getLevel();
        Long uid = changeUserLevel.getId();

        return userComponent.changeUserLevel(uid,level);
    }

    @PostMapping("/changeUserPwd")
    public ResponseEntity<Response> changeUserPwd(@RequestBody ChangeUserPwd changeUserPwd) {
        boolean flag = false;
        String token = changeUserPwd.getToken();
        Long uid = TokenUtils.decodeGetIdByToken(token);
        Long id = changeUserPwd.getId();
        String pwd = changeUserPwd.getPwd();

        if ((TokenUtils.verify(token,true) != null) || (TokenUtils.verify(token,false) != null && uid.equals(id))) {
            flag = true;
        }

        if(!flag){
            return ResponseEntityComponent.Token_Err;
        }

        return userComponent.changeUserPwd(id,pwd);
    }

    @PostMapping("/changeUserPwdByEmail")
    public ResponseEntity<Response> changeUserPwdByEmail(@RequestBody ChangeUserPwd changeUserPwd) throws MessagingException, UnsupportedEncodingException {
        String email = changeUserPwd.getEmail();
        String yzm = changeUserPwd.getYzm();
        String password = changeUserPwd.getPwd();

        return userComponent.changeUserPwdByEmail(email,yzm,password);
    }

    @PostMapping("/changeEmail")
    public ResponseEntity<Response> changeEmail(@RequestBody ChangeUserPwd changeUserPwd) throws MessagingException, UnsupportedEncodingException {
        String email = changeUserPwd.getEmail();
        String yzm = changeUserPwd.getYzm();
        String username = changeUserPwd.getUsername();

        return userComponent.changeEmail(username,email,yzm);
    }

    @PostMapping("/SendVerificationCode")
    public ResponseEntity<Response> SendVerificationCode(@RequestBody ChangeUserPwd user) throws MessagingException, UnsupportedEncodingException {
        String email = user.getEmail();
        return userComponent.SendVerificationCode(email);
    }

    @GetMapping("/deleteUser")
    public ResponseEntity<Response> deleteUser(@RequestParam("token") String token,@RequestParam("id") Long id) {
        Long uid = TokenUtils.decodeGetIdByToken(token);
        boolean flag = (TokenUtils.verify(token, true) != null) || (TokenUtils.verify(token, false) != null && uid.equals(id));

        if(!flag){
            return ResponseEntityComponent.Token_Err;
        }

        return userComponent.deleteUser(id);
    }

}

@Data
class ChangeUserLevel{
    private String token;
    private Long id;
    private PermissionLevelEnum level;
}

@Data
class ChangeUserPwd{
    private String token;
    private Long id;
    private String username;
    private String email;
    private String pwd;
    private String yzm;
}