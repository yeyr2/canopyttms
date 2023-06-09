package com.ttms.controller;

import com.ttms.component.ResponseEntityComponent;
import com.ttms.component.UserComponent;
import com.ttms.enums.PermissionLevelEnum;
import com.ttms.pojo.Response;
import com.ttms.pojo.TokenAndT;
import com.ttms.pojo.User;
import com.ttms.tools.Sha256Util;
import com.ttms.tools.TokenUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        user.setPassword(Sha256Util.getSHA256Str(user.getPassword()));

        return userComponent.login(user);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Response> Register(@RequestBody User user) {
        user.setPassword(Sha256Util.getSHA256Str(user.getPassword()));

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
    private String pwd;
}