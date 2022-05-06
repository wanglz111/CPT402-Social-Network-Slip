package com.xjtlu.slip.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjtlu.slip.pojo.Emotion;
import com.xjtlu.slip.service.EmotionService;
import com.xjtlu.slip.mapper.EmotionMapper;
import org.springframework.stereotype.Service;

/**
* @author wangluzhi
* @description 针对表【emotion】的数据库操作Service实现
* @createDate 2022-05-06 22:08:45
*/
@Service
public class EmotionServiceImpl extends ServiceImpl<EmotionMapper, Emotion>
    implements EmotionService{

}




