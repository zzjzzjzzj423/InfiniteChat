package com.zj.infinitechat.friendmomentservice.service;

import com.zj.infinitechat.friendmomentservice.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-02-08 18:52:05
*/
@Mapper
public interface UserService extends IService<User> {

}
