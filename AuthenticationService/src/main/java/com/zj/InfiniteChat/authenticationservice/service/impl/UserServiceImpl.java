package com.zj.InfiniteChat.authenticationservice.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.InfiniteChat.authenticationservice.Data.Common.Upload.UploadResponse;
import com.zj.InfiniteChat.authenticationservice.Data.Login.LoginResponse;
import com.zj.InfiniteChat.authenticationservice.Data.Register.RegisterRequest;
import com.zj.InfiniteChat.authenticationservice.Data.Register.RegisterResponse;
import com.zj.InfiniteChat.authenticationservice.Exception.DataBaseException;
import com.zj.InfiniteChat.authenticationservice.Exception.UserException;
import com.zj.InfiniteChat.authenticationservice.constants.Enum.ERROR_ENUMS;
import com.zj.InfiniteChat.authenticationservice.constants.LoginConstants;
import com.zj.InfiniteChat.authenticationservice.constants.OssConstants;
import com.zj.InfiniteChat.authenticationservice.constants.RegisterConstants;
import com.zj.InfiniteChat.authenticationservice.model.User;
import com.zj.InfiniteChat.authenticationservice.service.UserService;
import com.zj.InfiniteChat.authenticationservice.mapper.UserMapper;
import com.zj.InfiniteChat.authenticationservice.utils.JWTUtils;
import com.zj.InfiniteChat.authenticationservice.utils.OssUtils;
import com.zj.InfiniteChat.authenticationservice.utils.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static cn.hutool.core.lang.Console.log;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-01-01 22:43:40
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OssUtils ossUtils;

    public RegisterResponse register(RegisterRequest data){
        String email=data.getEmail();
        if(isRegister(email)){
            throw new UserException(ERROR_ENUMS.REGISTER_ERROR);
        }else{

           String code = stringRedisTemplate.opsForValue().get(RegisterConstants.EMAIL_PREFIX +email);
           if (code!=null && data.getCode().equals(code)){

               Snowflake snowflake= IdUtil.getSnowflake(1,1);
               String nickName= RandomGenerator.generatorNickName();
               String enPassword= DigestUtils.md5DigestAsHex(data.getPassword().getBytes());

               User saveUser=new User()
                       .setUserId(snowflake.nextId())
                       .setUserName(nickName)
                       .setEmail(data.getEmail())
                       .setPassword(enPassword)
                       .setPhone(data.getPhone());

               boolean isSave=this.save(saveUser);

               if (isSave){
                   return new RegisterResponse().setEmail(email).setMessage("注册成功").setSuccess(true);
               }else{
                    throw new DataBaseException("数据库存入失败");

               }

           }else{
               if (code==null){
                   throw new UserException(ERROR_ENUMS.REDISCODE_ERROR);
               }
               throw new UserException(ERROR_ENUMS.CODE_ERROR);

           }
        }


    }
    @Override
    public LoginResponse login(String email, String password){
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getEmail,email);
        User selectedUser=this.selectOnlyOne(lambdaQueryWrapper);
        String enPassword=DigestUtils.md5DigestAsHex(password.getBytes());
        if(selectedUser==null){
            throw new UserException(ERROR_ENUMS.USER_NOT_EXIST);
        }else if(!enPassword.equals(selectedUser.getPassword())){
            throw new UserException(ERROR_ENUMS.PASSWORD_NOT_TRUE);
        }
        String jwtToken= JWTUtils.generateJWT(selectedUser.getUserId());
        return new LoginResponse()
                .setUserId(String.valueOf(selectedUser.getUserId()))
                .setUserName(selectedUser.getUserName())
                .setStatus(selectedUser.getStatus())
                .setAvatar(selectedUser.getAvatar())
                .setGender(selectedUser.getGender())
                .setToken(jwtToken)
                .setSignature(selectedUser.getSignature());
    }

    @Override
    public LoginResponse loginByCode(String email, String code) {
        String checkedCode=stringRedisTemplate.opsForValue().get(LoginConstants.LOGIN_PREFIX+email);
        if (!checkedCode.equals(code)){
            throw  new UserException(ERROR_ENUMS.LOGIN_CODE_ERROR);
        }
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getEmail,email);
        User selectedUser=this.selectOnlyOne(lambdaQueryWrapper);
        String jwtToken= JWTUtils.generateJWT(selectedUser.getUserId());
        return new LoginResponse()
                .setUserId(String.valueOf(selectedUser.getUserId()))
                .setUserName(selectedUser.getUserName())
                .setStatus(selectedUser.getStatus())
                .setAvatar(selectedUser.getAvatar())
                .setGender(selectedUser.getGender())
                .setToken(jwtToken)
                .setSignature(selectedUser.getSignature());
    }

    @Override
    public UploadResponse loadAvatar(String filename) {
        String downloadUrl= ossUtils.getDownloadUrl(filename);
        String uploadUrl= ossUtils.uploadUrl(filename, OssConstants.PICTURE_TIME);


        return new UploadResponse().setUploadUrl(uploadUrl).setDownloadUrl(downloadUrl);
    }

    private boolean isRegister(String email){

        LambdaQueryWrapper<User> numbersCount=new LambdaQueryWrapper<>();
        numbersCount.eq(User::getEmail, email);

        long count=this.count(numbersCount);

        return count>0;
    }





}



