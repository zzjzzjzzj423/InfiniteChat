package com.zj.infinitechat.offlinedatastoreservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.infinitechat.offlinedatastoreservice.model.UserSession;
import com.zj.infinitechat.offlinedatastoreservice.service.UserSessionService;
import com.zj.infinitechat.offlinedatastoreservice.mapper.UserSessionMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【user_session】的数据库操作Service实现
* @createDate 2026-02-05 19:33:23
*/
@Service
public class UserSessionServiceImpl extends ServiceImpl<UserSessionMapper, UserSession>
    implements UserSessionService{

    @Override
    public Set<Long> findSessionListByUserId(long userid , List<UserSession> userSessionList) {
        LambdaQueryWrapper<UserSession> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSession::getUserId , userid);
        userSessionList = this.list(lambdaQueryWrapper);
        List<Long> ids = new ArrayList<>();
        for(UserSession item : userSessionList){
            ids.add(item.getSessionId());
        }

        return new HashSet<>(ids);
    }
}




