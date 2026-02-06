package com.zj.infinitechat.offlinedatastoreservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.infinitechat.offlinedatastoreservice.model.Session;
import com.zj.infinitechat.offlinedatastoreservice.service.SessionService;
import com.zj.infinitechat.offlinedatastoreservice.mapper.SessionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【session(会话表)】的数据库操作Service实现
* @createDate 2026-02-05 19:32:55
*/
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session>
    implements SessionService{


}




