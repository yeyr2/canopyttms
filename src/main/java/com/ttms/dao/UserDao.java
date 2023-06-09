package com.ttms.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ttms.enums.PermissionLevelEnum;
import com.ttms.pojo.User;
import com.ttms.tools.Sha256Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserDao {
    public User judgeUser(String username,Boolean isAdmin) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>(User.class);
        wrapper.eq(User::getUsername,username);
        if (isAdmin != null) {
            wrapper.eq(User::getAdmin,isAdmin);
        }
        return Db.getOne(wrapper);
    }

    public Boolean saveUser(User user) {
        return Db.save(user);
    }

    public boolean update(User user){
        return Db.updateById(user);
    }

    @Transactional
    public User getUser(Long id) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>(User.class);
        wrapper.eq(User::getId,id);
        return Db.getOne(wrapper);
    }

    public boolean verify(User user){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>(User.class);
        queryWrapper.eq(User::getUsername,user.getUsername());
        List<User> list = Db.list(queryWrapper);
        if(list != null || list.size() != 0){
            return false;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>(User.class);
        wrapper.eq(User::getId,user.getId());
        User two = Db.getOne(wrapper);

        return two.normalEqual(user);
    }

    public boolean changeLevelByUid(Long uid, PermissionLevelEnum level) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>(User.class);
        wrapper.eq(User::getId,uid);
        wrapper.set(User::getPermissionLevel,level);
        if(level.getId() >= 1){
            wrapper.set(User::getAdmin,true);
        }else{
            wrapper.set(User::getAdmin,false);
        }

        return Db.update(wrapper);
    }

    public List<User> getUsers(List<Long> ids, Boolean isAdmin) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>(User.class);
        if(!(ids == null || ids.size() == 0)){//不查询全部
            wrapper.in(User::getId,ids);
        }
        if(isAdmin != null){
            wrapper.eq(User::getAdmin,isAdmin);
        }

        return Db.list(wrapper);
    }

    public boolean changePassword(Long uid, String pwd) {
        String password = Sha256Util.getSHA256Str(pwd);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>(User.class);
        wrapper.eq(User::getId,uid);
        wrapper.set(User::getPassword,password);

        return Db.update(wrapper);
    }

    public List<User> getUserByName(String name, Boolean isAdmin) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>(User.class);
        wrapper.like(User::getUsername,name);
        if(isAdmin != null){
            wrapper.eq(User::getAdmin,isAdmin);
        }

        return Db.list(wrapper);
    }

    public boolean deleteUserById(Long id) {
        return Db.removeById(id,User.class);
    }
}
