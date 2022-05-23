package com.xjtlu.slip.service;

import com.xjtlu.slip.pojo.Emotion;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface EmotionService extends IService<Emotion> {

    List<Emotion> getByUserId(Long id);

    List<Emotion> getByUserIdForIndex(Long id);
}
