package com.ttms.component;

import com.ttms.dao.UserDao;
import com.ttms.enums.PermissionLevelEnum;
import com.ttms.pojo.Response;
import com.ttms.pojo.User;
import com.ttms.tools.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserComponent {

    @Autowired
    private UserDao userDao;

    public ResponseEntity<Response> login(User user){
        User newUser = userDao.judgeUser(user.getUsername(),null);
        if ( newUser == null) {
            return ResponseEntityComponent.User_Not_Exist;
        }

        String token = TokenUtils.getToken(newUser);
        Response response = new Response().setMsg("登录成功").setValue(newUser).setToken(token);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> register(User user) {
        if(userDao.judgeUser(user.getUsername(),null) != null) {
            return ResponseEntityComponent.User_Exist;
        }

        // 添加初始基本信息：
        user.setDescription("这个人睡着了。");
        user.setSex("未知");

        if (!userDao.saveUser(user)) {
            return ResponseEntityComponent.Create_Failed("user");
        }

        Response response = new Response().setMsg("creat user successes,password:"+user.getPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> getUserInfo(Long id) {
        // 请求用户信息
        User user = userDao.getUser(id);
        Response response = new Response();
        response.setValue(user);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> changeUserInfo(User user){
        if(user.getId() == null || user.getId() <= 0){
            return ResponseEntityComponent.ID_ERR;
        }
        user.setPermissionLevel(null);
        user.setAdmin(null);
        user.setPassword(null);
        user.setBalance(null);
        //验证是否有非法信息
        if (!user.verify()){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        //验证是否重复
        if(userDao.verify(user)){
            return ResponseEntityComponent.USER_CHANGE_ERR;
        }

        if(!userDao.update(user)){
            return ResponseEntityComponent.Update_Failed("user");
        }

        Response response = new Response();
        response.setValue(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> changeUserLevel(Long uid,PermissionLevelEnum level) {
        if(uid == null || uid <= 0){
            return ResponseEntityComponent.ID_ERR;
        }

        if(level == null || !level.verify()){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        if(!userDao.changeLevelByUid(uid,level)){
            return ResponseEntityComponent.Update_Failed("user level");
        }

        Response response = new Response();
        response.setValue(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> getUsersInfo(List<Long> ids, Boolean isAdmin){
        List<User> users = userDao.getUsers(ids,isAdmin);

        Response response = new Response();
        response.setValue(users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> changeUserPwd(Long uid, String pwd) {
        if(uid == null || uid <= 0){
            return ResponseEntityComponent.ID_ERR;
        }

        if(pwd == null){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        String replace = pwd.replace(" ", "");
        if ("".equals(replace) || replace.length() < 6) {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        if(!userDao.changePassword(uid,pwd)){
            return ResponseEntityComponent.Update_Failed("user password");
        }

        Response response = new Response();
        response.setValue(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> getUserByName(String name, Boolean isAdmin) {
        if(name == null){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        List<User> userByName = userDao.getUserByName(name,isAdmin);

        Response response = new Response();
        response.setValue(userByName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> deleteUser(Long id) {
        if(!userDao.deleteUserById(id)){
            return ResponseEntityComponent.Delete_Failed("user");
        }

        Response response = new Response();
        response.setValue(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
