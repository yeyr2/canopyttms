package com.ttms.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ttms.enums.SeatStatus;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Studio {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(value = "name")
    private String name;
    @TableField(value = "StudioRows")
    @JsonProperty("rows")
    private Integer StudioRows;
    @TableField(value = "StudioColumns")
    @JsonProperty("columns")
    private Integer StudioColumns;
    private String description;

    @TableLogic
    private Integer isDeleted;

    public Studio() {}

    public Studio getStudio(String name,Integer StudioRows,Integer StudioColumns,String description) {
        this.name = name;
        this.StudioRows = StudioRows;
        this.StudioColumns = StudioColumns;
        this.description = description;
        return this;
    }

    public boolean verify(String type) {
        switch (type) {
            case "insert":
                this.setId(null);
                if (this.getStudioColumns() == null || this.getStudioRows() == null || this.getName() == null) {
                    return false;
                }
                if(this.getStudioColumns() <= 0|| this.getStudioRows() <= 0) {
                    return false;
                }
                break;
            case "update":
                if(this.getId() == null || this.getId() <= 0) {
                    return false;
                }
                this.setStudioColumns(null);
                this.setStudioRows(null);
                break;
            default:
                break;
        }
        return true;
    }


}
