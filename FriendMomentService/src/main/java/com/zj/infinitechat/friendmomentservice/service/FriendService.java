package com.zj.infinitechat.friendmomentservice.service;

import com.zj.infinitechat.friendmomentservice.model.Friend;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【friend(联系人表)】的数据库操作Service
* @createDate 2026-02-08 18:51:49
*/
@Mapper
public interface FriendService extends IService<Friend> {

}
