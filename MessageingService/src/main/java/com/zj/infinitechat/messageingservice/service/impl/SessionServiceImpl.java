package com.zj.infinitechat.messageingservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.infinitechat.messageingservice.model.Session;
import com.zj.infinitechat.messageingservice.mapper.SessionMapper;
import com.zj.infinitechat.messageingservice.service.SessionService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【session(会话表)】的数据库操作Service实现
* @createDate 2026-01-28 16:06:45
*/
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session>
    implements SessionService {

}




