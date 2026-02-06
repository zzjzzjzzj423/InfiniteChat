package com.zj.infinitechat.offlinedatastoreservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.infinitechat.offlinedatastoreservice.model.User;
import com.zj.infinitechat.offlinedatastoreservice.service.UserService;
import com.zj.infinitechat.offlinedatastoreservice.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-02-05 19:33:20
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




