package com.xjtlu.slip.service;

import com.xjtlu.slip.pojo.Topic;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【topic】的数据库操作Service
* @createDate 2022-04-24 20:29:35
*/
@Repository
public interface TopicService extends IService<Topic> {

    List<Topic> getAllTopicsAndUser();

}
