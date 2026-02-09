package com.zj.infinitechat.friendmomentservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.infinitechat.friendmomentservice.model.User;
import com.zj.infinitechat.friendmomentservice.service.UserService;
import com.zj.infinitechat.friendmomentservice.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-02-08 18:52:05
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




