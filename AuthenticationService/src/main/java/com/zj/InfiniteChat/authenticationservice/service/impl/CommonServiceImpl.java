package com.zj.InfiniteChat.authenticationservice.service.impl;

import com.zj.InfiniteChat.authenticationservice.Data.Common.CommonResponse;
import com.zj.InfiniteChat.authenticationservice.constants.LoginConstants;
import com.zj.InfiniteChat.authenticationservice.constants.RegisterConstants;
import com.zj.InfiniteChat.authenticationservice.service.CommonService;
import com.zj.InfiniteChat.authenticationservice.utils.RandomGenerator;
import com.zj.InfiniteChat.authenticationservice.utils.SendMailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public CommonResponse sendEmail(String email) {
        String code= RandomGenerator.generatorRandomCode();

        SendMailUtil.sendEmailCode(email,code);
        stringRedisTemplate.opsForValue().set(RegisterConstants.EMAIL_PREFIX +email,code,RegisterConstants.DURATION_TIME, TimeUnit.MINUTES);
        return new CommonResponse().setEmail(email);
    }

    @Override
    public CommonResponse sendEmailLogin(String email) {
        String code=RandomGenerator.generatorRandomCode();
        SendMailUtil.sendEmailCode(email,code);
        stringRedisTemplate.opsForValue().set(LoginConstants.LOGIN_PREFIX+email,code,LoginConstants.DURATION_TIME,TimeUnit.MINUTES);
        return new CommonResponse().setEmail(email);

    }


}
