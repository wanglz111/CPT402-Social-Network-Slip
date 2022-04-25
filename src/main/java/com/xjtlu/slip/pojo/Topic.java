package com.xjtlu.slip.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Delete;

/**
 *
 * @TableName topic
 */
@TableName(value ="topic")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic implements Serializable {
    /**
     * topic index
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * topic title
     */
    private String title;

    /**
     * topic body
     */
    private String content;

    /**
     *
     */
    private Long authorId;

    /**
     *
     */
    private String author;

    /**
     *
     */
    private Long createUnixTime;

    /**
     *
     */
    private Long updateUnixTime;

    /**
     *
     */
    private Long latestCommentUnixTime;

    /**
     * type of topic
     */
    private String type;

    /**
     *
     */
    private Integer view;

    /**
     *
     */
    private Integer comment;

    /**
     *
     */
    @TableLogic
    private String isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
