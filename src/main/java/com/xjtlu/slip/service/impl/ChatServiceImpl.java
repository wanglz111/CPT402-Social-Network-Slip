package com.xjtlu.slip.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjtlu.slip.pojo.Chat;
import com.xjtlu.slip.service.ChatService;
import com.xjtlu.slip.mapper.ChatMapper;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
    implements ChatService{

}




