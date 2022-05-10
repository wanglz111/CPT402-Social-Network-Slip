package com.xjtlu.slip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjtlu.slip.pojo.Emotion;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.EmotionService;
import com.xjtlu.slip.mapper.EmotionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【emotion】的数据库操作Service实现
* @createDate 2022-05-06 22:08:45
*/
@Service
public class EmotionServiceImpl extends ServiceImpl<EmotionMapper, Emotion>
    implements EmotionService{

    @Override
    public List<Emotion> getByUserId(Long id) {
        QueryWrapper<Emotion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        return baseMapper.selectList(queryWrapper);
    }
}




