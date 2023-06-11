package com.ttms.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ttms.tools.StringUtil;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("plan")
public class Plan {
    private Long id;
    private Long videoId;
    private String video;
    @TableField("time")
    private Long releaseTime;
    private String language;
    private String studio;
    private Long studioId;
    private Double price;
    // 例如： <1排1列,ticket>
    @TableField(exist = false)
    private List<Ticket> tickets;  // 关联票
    // 根据videoId和studioId获取票
    @TableLogic
    private Integer isDeleted;

    public boolean verify(String type) {
        if ("insert".equals(type)) {
            if(studio == null || price == null || video == null ) {
                return false;
            }
            studio = StringUtil.removeSpaces(studio);
            video = StringUtil.removeSpaces(video);
            if (language != null){
                language = StringUtil.removeSpaces(language);
            }else{
                language = "未知";
            }
        }
        if("update".equals(type)) {
            return video != null;
        }

        return true;
    }
}
