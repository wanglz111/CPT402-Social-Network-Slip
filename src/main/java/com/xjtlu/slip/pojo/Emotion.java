package com.xjtlu.slip.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName emotion
 */
@TableName(value ="emotion")
@Data
public class Emotion implements Serializable {
    /**
     * Primary Key
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * user_id
     */
    private Long userId;

    /**
     * emotion content
     */
    private String content;

    /**
     * create_time
     */
    private Long createTime;

    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
