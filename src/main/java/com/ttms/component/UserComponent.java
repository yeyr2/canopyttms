package com.ttms.component;

import com.ttms.dao.UserDao;
import com.ttms.enums.PermissionLevelEnum;
import com.ttms.pojo.Mail;
import com.ttms.pojo.Response;
import com.ttms.pojo.User;
import com.ttms.tools.Sha256Util;
import com.ttms.tools.StringUtil;
import com.ttms.tools.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserComponent {

    public static ConcurrentHashMap<Long,String> verificationCode__tmp;
    public static ConcurrentHashMap<Long,String> changeEmail__tmp;
    @Autowired
    private UserDao userDao;

    public ResponseEntity<Response> login(User user){
        if(user == null || user.getUsername() == null || user.getPassword() == null){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }
        user.setUsername(StringUtil.removeSpaces(user.getUsername()));
        user.setPassword(Sha256Util.getSHA256Str(user.getPassword()));

        User newUser = userDao.judgeUser(user.getUsername(),null);
        if ( newUser == null) {
            return ResponseEntityComponent.User_Not_Exist;
        }

        String token = TokenUtils.getToken(newUser);
        Response response = new Response().setMsg("登录成功").setValue(newUser).setToken(token);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> register(User user) {
        if(user == null || user.getUsername() == null || user.getPassword() == null){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }
        user.setUsername(StringUtil.removeSpaces(user.getUsername()));
        if(user.getPassword().contains(" ")){
            return ResponseEntityComponent.Password_Err("设置密码不允许出现空格");
        }
        user.setPassword(Sha256Util.getSHA256Str(user.getPassword()));

        if(userDao.judgeUser(user.getUsername(),null) != null) {
            return ResponseEntityComponent.User_Exist;
        }

        // 添加初始基本信息：
        user.setDescription("这个人睡着了。");
        user.setSex("未知");

        if (!userDao.saveUser(user)) {
            return ResponseEntityComponent.Create_Failed("user");
        }

        Response response = new Response().setMsg("creat user success,user:"+user.getUsername());
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
        user.setEmail(null);
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

    public ResponseEntity<Response>
    changeUserLevel(Long uid,PermissionLevelEnum level) {
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

        String password = StringUtil.removeSpaces(pwd);
        if (password.length() < 6) {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        if(!userDao.changePassword(uid,Sha256Util.getSHA256Str(pwd))){
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

        List<User> userByName = userDao.getUserByLikeName(name,isAdmin);

        Response response = new Response();
        response.setValue(userByName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> SendVerificationCode(String email) throws MessagingException, UnsupportedEncodingException {
        email = StringUtil.removeSpaces(email);

        if (!Mail.formatCheck(email)) {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        // 获取用户信息
        User user = userDao.getUserByEmail(email);
        if(user == null){
            return ResponseEntityComponent.User_Exist;
        }

        new MailComponent().sendMail(email,null,user,"code");

        Response response = new Response();
        response.setValue(true);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    public ResponseEntity<Response> changeUserPwdByEmail(String email, String yzm, String password) throws MessagingException, UnsupportedEncodingException {
        if(email == null || email.isEmpty() || yzm == null || yzm.length() != 5){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        // 获取用户信息
        User user = userDao.getUserByEmail(email);
        if(user == null){
            return ResponseEntityComponent.User_Exist;
        }

        String code = verificationCode__tmp.get(user.getId()).substring(0,5);
        verificationCode__tmp.remove(user.getId());

        if(!code.equals(yzm)){
            return ResponseEntityComponent.verificationCode_Err();
        }

        if(password == null || password.isEmpty()){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }
        password = StringUtil.removeSpaces(password);
        if (password.length() < 6) {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        if(!userDao.changePassword(user.getId(),Sha256Util.getSHA256Str(password))){
            return ResponseEntityComponent.Update_Failed("user password");
        }
        new MailComponent().sendMail(email,password,user,"password");

        Response response = new Response();
        response.setValue(true);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    public ResponseEntity<Response> changeEmail(String username,String email, String yzm) throws MessagingException, UnsupportedEncodingException {
        if(email == null || email.isEmpty() || yzm == null || yzm.length() != 5){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        // 获取用户信息
        User user = userDao.getUserByName(username);
        if(user == null){
            return ResponseEntityComponent.User_Exist;
        }

        if(!userDao.changeEmail(user.getId(),email)){
            return ResponseEntityComponent.Update_Failed("user email");
        }

        new MailComponent().sendMail(email,null,user,"email");

        Response response = new Response();
        response.setValue(true);
        return new ResponseEntity<>(response,HttpStatus.OK);
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
