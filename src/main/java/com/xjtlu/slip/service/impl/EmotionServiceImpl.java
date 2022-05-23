package com.xjtlu.slip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjtlu.slip.pojo.Emotion;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.EmotionService;
import com.xjtlu.slip.mapper.EmotionMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EmotionServiceImpl extends ServiceImpl<EmotionMapper, Emotion>
    implements EmotionService{

    @Override
    public List<Emotion> getByUserId(Long id) {
        QueryWrapper<Emotion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Emotion> getByUserIdForIndex(Long id) {
        QueryWrapper<Emotion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit 10");
        return baseMapper.selectList(queryWrapper);
    }
}




