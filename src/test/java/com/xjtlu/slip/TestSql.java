package com.xjtlu.slip;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.mapper.CommentMapper;
import com.xjtlu.slip.mapper.TopicMapper;
import com.xjtlu.slip.mapper.UserMapper;
import com.xjtlu.slip.pojo.Comment;
import com.xjtlu.slip.pojo.Topic;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.CommentService;
import com.xjtlu.slip.service.TopicService;
import com.xjtlu.slip.service.UserService;
import com.xjtlu.slip.utils.TimeFormat;
import com.xjtlu.slip.vo.CommentCount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xjtlu.slip.utils.TimeToUnix.getCurrentTime;

@SpringBootTest
public class TestSql {
    @Autowired
    private TopicService topicService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void InsertTopics() throws Exception {
        ArrayList<Topic> topics = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Topic topic = new Topic();
            topic.setTitle("test" + i);
            topic.setContent("test content" + i);
            topic.setAuthorId(1L);
            topic.setAuthor("wangluzhi");
            //获取一个3000-10000的随机数
            int number = (int) (Math.random() * (10000 - 3000 + 1) + 3000);

            topic.setCreateUnixTime(getCurrentTime() - number);
            topic.setLatestCommentUnixTime(getCurrentTime() - number);
            topics.add(topic);
        }

//        topicService.saveOrUpdateBatch(topics);

    }

    @Test
    public void testComment() {
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.eq("user_id", 1);
        System.out.println(commentService.list(query));
    }

    @Test
    public void testSelectCommentAndUser() {
        System.out.println(commentService.getListByTopicId("85"));
    }

    @Test
    public void testSetCommentTime() {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("latest_comment_unix_time");
        Page<Topic> page = new Page<>(1, 100);
        List<Topic> topics = topicService.page(page, queryWrapper).getRecords();
        topics.forEach(topic -> {
            Long now = getCurrentTime();
//           System.out.println(now);
            System.out.println(now - topic.getLatestCommentUnixTime());
            long time = (now - topic.getLatestCommentUnixTime()) * 1000;
            System.out.println(TimeFormat.format(time));
        });
    }

    @Test
    public void testDate() {
        System.out.println(new Date().getTime());
    }

    @Test
    public void testSelectTopicAndUser() {
        List<Topic> topics = topicMapper.getAllTopicsAndUser();
        topics.forEach(System.out::println);
    }

    @Test
    public void select() {
        // 创建分页参数
        Page<Topic> page = new Page<>(1, 2);
        // 创建查询条件
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user.id", 1);
        IPage<Topic> result = topicMapper.getAllTopicsAndUserByPage(page, queryWrapper);
        // 获取数据
//        List<Topic> records = result.getRecords();
//        records.forEach(System.out::println);
//        System.out.println("总页数 = "+ result.getPages());
        System.out.println(result.getRecords().size());
    }

    @Test
    public void testCommentCount() {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        Page<Topic> page = new Page<>(1, 166);
        queryWrapper.orderByDesc("latest_comment_unix_time");
        IPage<Topic> result = topicMapper.getAllTopicsAndAllComments(page, queryWrapper);
        List<Topic> records = result.getRecords();
        records.forEach(System.out::println);
    }

    @Test
    public void testCommentCount2() {
        Map<Long, CommentCount> topicCommentCount = topicMapper.getTopicCommentsCount();
        CommentCount commentCount = topicCommentCount.get(1L);
        System.out.println(commentCount.getCommentCount());
    }

    @Test
    public void testLatestCommentInfo() {
        Map<Long, Comment> topicCommentInfo = commentMapper.selectLatestCommentEveryTopic();
        topicCommentInfo.forEach((key, value) -> {
            System.out.println(key + ":" + value.getContent());
        });
    }

    @Autowired
    private UserService userService;

    @Test
    public void userListTest() {
        Map<Long, User> users = userService.getUserMap();
        System.out.println(users.get(100L).getName());
    }

    @Test
    public void testSelectTopic() {
        System.out.println(topicMapper.selectById(1L));
    }

    @Test
    public void testCommentAndUser() {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment.id", 1L);
        Comment comment = commentMapper.selectOneCommentAndUser(queryWrapper);
        System.out.println(comment.getUser().getName());
    }

    @Test
    public void testUpdateUser() {
        List<User> users = userService.list();
        users.forEach(user -> {
            String avatar = user.getAvatar();
            String replaceAvatar = avatar.replace("http://ran6e8ncl.bkt.clouddn.com/", "http://img.cpt402.fun/");
            user.setAvatar(replaceAvatar);
            });
        userService.updateBatchById(users);
    }

    @Test
    public void testUpdateUserPassword() {
        List<User> users = userService.list();
        users.forEach(user -> {
            String password = user.getPassword();
            user.setPassword(MD5.create().digestHex(password));
        });
        userService.updateBatchById(users);
    }

    @Test
    public void updateTopicCommentCount() {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        Page<Topic> page = new Page<>(1, 166);
        queryWrapper.orderByDesc("latest_comment_unix_time");
        IPage<Topic> result = topicMapper.getAllTopicsAndAllComments(page, queryWrapper);
        List<Topic> records = result.getRecords();
        records.forEach(topic -> {
            Integer commentCount = topic.getCommentCount();
            topic.setComment(commentCount);
            topicMapper.updateById(topic);
        });
    }
}
