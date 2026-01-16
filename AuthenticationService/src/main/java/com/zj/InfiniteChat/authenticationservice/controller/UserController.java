package com.zj.InfiniteChat.authenticationservice.controller;


import com.zj.InfiniteChat.authenticationservice.Data.Common.Upload.UploadRequest;
import com.zj.InfiniteChat.authenticationservice.Data.Common.Upload.UploadResponse;
import com.zj.InfiniteChat.authenticationservice.Data.Login.LoginCodeRequest;
import com.zj.InfiniteChat.authenticationservice.Data.Login.LoginRequest;
import com.zj.InfiniteChat.authenticationservice.Data.Login.LoginResponse;
import com.zj.InfiniteChat.authenticationservice.Data.Register.RegisterRequest;
import com.zj.InfiniteChat.authenticationservice.Data.Register.RegisterResponse;
import com.zj.InfiniteChat.authenticationservice.commom.Result;
import com.zj.InfiniteChat.authenticationservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/User")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping("/Register")
    public Result<RegisterResponse> register(@RequestBody RegisterRequest data){
        RegisterResponse response=userService.register(data);

        return Result.ok(response);

    }

    @GetMapping("Login")
    public Result<LoginResponse> login(@RequestBody LoginRequest data){
        LoginResponse loginResponse=userService.login(data.getEmail(), data.getPassword());
        return Result.ok(loginResponse);
    }

    @GetMapping("LoginByCode")
    public Result<LoginResponse> loginByCode(@RequestBody LoginCodeRequest data){
        LoginResponse loginResponse=userService.loginByCode(data.getEmail(), data.getCode());
        return Result.ok(loginResponse);
    }
    @GetMapping("/uploadAvatar")
    public Result<UploadResponse> uploadAvatar(@Valid UploadRequest uploadRequest){
        UploadResponse uploadResponse=userService.loadAvatar(uploadRequest.getFilename());

        return Result.ok(uploadResponse);
    }



}
