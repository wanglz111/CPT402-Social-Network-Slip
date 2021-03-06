package com.xjtlu.slip.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.List;

import com.xjtlu.slip.enums.TopicEnum;
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
    private Long latestCommentUnixTime;

    /**
     * type of topic
     */
    private TopicEnum type;

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

    @TableField(exist = false)
    private String latestCommentTime;

    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    List<Comment> comments;

    @TableField(exist = false)
    Integer commentCount;

    @TableField(exist = false)
    Comment latestComment;

    @TableField(exist = false)
    User latestReplyUser;
}
