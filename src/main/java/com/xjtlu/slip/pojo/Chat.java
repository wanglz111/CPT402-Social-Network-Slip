package com.xjtlu.slip.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName chat
 */
@TableName(value ="chat")
@Data
public class Chat implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long senderUserId;

    /**
     * 
     */
    private Long receiverUserId;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private Long timestamp;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}