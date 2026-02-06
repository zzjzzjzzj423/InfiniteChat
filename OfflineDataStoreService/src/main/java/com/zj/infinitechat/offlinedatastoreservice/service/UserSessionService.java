package com.zj.infinitechat.offlinedatastoreservice.service;

import com.zj.infinitechat.offlinedatastoreservice.model.UserSession;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
* @author Administrator
* @description 针对表【user_session】的数据库操作Service
* @createDate 2026-02-05 19:33:23
*/
public interface UserSessionService extends IService<UserSession> {
    public Set<Long> findSessionListByUserId(long userid , List<UserSession> list);
}
