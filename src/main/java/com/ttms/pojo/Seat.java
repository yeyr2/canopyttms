package com.ttms.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ttms.enums.SeatStatus;
import lombok.Data;

@Data
@TableName("seat")
public class Seat {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studioId;
    private Integer SeatRow;
    private Integer SeatColumn;

    /**
     * @0:维修中，1:没有，2:已被占据，3:空位
     */
    private SeatStatus status;
}
