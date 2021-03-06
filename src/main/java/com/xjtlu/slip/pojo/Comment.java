package com.xjtlu.slip.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName comment
 */
@TableName(value ="comment")
@Data
public class Comment implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    private Long topicId;

    /**
     *
     */
    private Long userId;

    /**
     * Comment body
     */
    private String content;

    /**
     *
     */
    private Long createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private Topic topic;

    @TableField(exist = false)
    private String time;
}
