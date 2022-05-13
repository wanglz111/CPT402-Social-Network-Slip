package com.xjtlu.slip.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName friendship
 */
@TableName(value ="friendship")
@Data
public class Friendship implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long pairId;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long friendId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}