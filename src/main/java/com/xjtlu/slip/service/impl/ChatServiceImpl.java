package com.xjtlu.slip.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjtlu.slip.pojo.Chat;
import com.xjtlu.slip.service.ChatService;
import com.xjtlu.slip.mapper.ChatMapper;
import org.springframework.stereotype.Service;

/**
* @author wangluzhi
* @description 针对表【chat】的数据库操作Service实现
* @createDate 2022-05-13 21:44:33
*/
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
    implements ChatService{

}




