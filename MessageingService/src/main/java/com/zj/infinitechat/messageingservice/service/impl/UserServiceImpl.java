package com.zj.infinitechat.messageingservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.infinitechat.messageingservice.model.User;
import com.zj.infinitechat.messageingservice.mapper.UserMapper;
import com.zj.infinitechat.messageingservice.service.UserService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-01-28 16:06:50
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




