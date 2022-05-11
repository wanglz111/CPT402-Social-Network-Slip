package com.xjtlu.slip.service;

import com.xjtlu.slip.pojo.Emotion;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【emotion】的数据库操作Service
* @createDate 2022-05-06 22:08:45
*/
public interface EmotionService extends IService<Emotion> {

    List<Emotion> getByUserId(Long id);

    List<Emotion> getByUserIdForIndex(Long id);
}
