package com.ttms.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ttms.enums.VideoType;
import com.ttms.tools.StringUtil;
import com.ttms.tools.TimeUtil;
import lombok.Data;

import java.text.ParseException;
import java.util.List;

@Data
@TableName("video")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Video {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "name", fill= FieldFill.INSERT)
    private String name;

    @TableField(value = "imageUrl", fill= FieldFill.INSERT)
    private String imgUrl;
//    type 为多个 VideoType 的组合
    @TableField(value = "type", fill= FieldFill.INSERT)
    private String type;
//      来源地区
    private String source;
//      时长
    private String duration;
    private Long releaseTime;

    private String description;

    @TableField(value = "score", fill= FieldFill.INSERT)
    private Double score;

    @TableField(exist = false)
    private List<String> actorList;

    @JsonIgnore
    private String actors;

    private Boolean hot;

    private Boolean comingSoon;

    @TableLogic
    private Integer isDeleted;


    public boolean verify(String type,String time) throws ParseException {
        if ("insert".equals(type)){
            //todo : 文件上传
            if(this.name == null || this.imgUrl == null ) {
                return false;
            }
            if(time == null) {
                this.releaseTime = System.currentTimeMillis();
            }else{
                this.releaseTime = TimeUtil.getDateByFormat(time);
            }
            if(this.type == null) {
                this.type = VideoType.Other.getType();
            }
            if(this.duration == null) {
                this.duration = "100分钟";
            }
            if(this.score == null) {
                this.score = 0.0;
            }
            if(this.actorList == null) {
                this.actors = "";
            } else{
                this.actors = StringUtil.getString(this.actorList);
            }
            this.hot = false;
            this.comingSoon = false;
        }
        if("update".equals(type)) {
            if(this.actorList != null) {
                this.actors = StringUtil.getString(this.actorList);
            }
        }

        return true;
    }
}
