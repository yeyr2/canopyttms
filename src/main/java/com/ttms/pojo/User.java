package com.ttms.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ttms.enums.PermissionLevelEnum;
import com.ttms.tools.StringUtil;
import lombok.Data;

@TableName("user")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"password"},allowSetters = true)
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;

    private String password;
    private String sex;
    private Integer age;
    @TableField(value = "is_admin")
    private Boolean admin;

    private Double balance;//个人账号余额

    private Long phone;

    private String birthday;
    private String hobbies;
    private String description;

    private PermissionLevelEnum permissionLevel;
    @TableLogic
    @JsonIgnoreProperties
    private Integer isDeleted;

    public boolean verify(){
        this.setUsername(StringUtil.removeSpaces(this.getUsername()));
        if ("".equals(this.getUsername())) {
            return false;
        }

        if(this.phone != null && String.valueOf(this.phone).length() < 11){
            return false;
        }

        this.setSex(StringUtil.removeSpaces(this.getSex()));
        if(!("男".equals(this.sex) || "女".equals(this.sex))){
            return false;
        }

        if(age == null){
            age = 0;
        }

        return true;
    }

    public boolean normalEqual(User user){
        return user.getHobbies().equals(this.getHobbies()) && user.getAge().equals(this.getAge())
                && user.getSex().equals(this.getSex()) && user.getDescription().equals(this.getDescription())
                && user.getBirthday().equals(this.getBirthday()) && user.getPhone().equals(this.getPhone())
                && user.getUsername().equals(this.getUsername());
    }
}
