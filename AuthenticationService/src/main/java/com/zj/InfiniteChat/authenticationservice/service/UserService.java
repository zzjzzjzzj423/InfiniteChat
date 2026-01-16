package com.zj.InfiniteChat.authenticationservice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zj.InfiniteChat.authenticationservice.Data.Common.Avatar.Update.UpdateAvtarResponse;
import com.zj.InfiniteChat.authenticationservice.Data.Common.Avatar.Upload.UploadResponse;
import com.zj.InfiniteChat.authenticationservice.Data.Login.LoginResponse;
import com.zj.InfiniteChat.authenticationservice.Data.Register.RegisterRequest;
import com.zj.InfiniteChat.authenticationservice.Data.Register.RegisterResponse;
import com.zj.InfiniteChat.authenticationservice.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-01-01 22:43:40
*/
public interface UserService extends IService<User> {

    default public User selectOnlyOne(LambdaQueryWrapper<User> lambdaQueryWrapper){
        lambdaQueryWrapper.last("limit 1");
        return this.getOne(lambdaQueryWrapper);


    }

    public RegisterResponse register(RegisterRequest data);


    public LoginResponse login(String email, String password);

    public LoginResponse loginByCode(String email, String code);

    public UploadResponse loadAvatar(String filename);

    public UpdateAvtarResponse updateAvatar(String userid, String downloadUrl);
}
