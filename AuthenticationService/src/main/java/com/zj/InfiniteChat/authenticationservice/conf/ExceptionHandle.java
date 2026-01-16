package com.zj.InfiniteChat.authenticationservice.conf;


import com.zj.InfiniteChat.authenticationservice.Exception.CodeException;
import com.zj.InfiniteChat.authenticationservice.commom.Result;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.zj.InfiniteChat.authenticationservice.Exception.UserException;


@Slf4j
@RestControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = UserException.class)
    public Result<?> handleUserException(UserException userException){
        log.error("用户错误");
        return new Result<>().setCode(userException.getCode());

    }

    @ExceptionHandler(value = CodeException.class)
    public Result<?> handleCodeException(CodeException codeException){
        log.error("验证码错误");
        return new Result<>().setCode(codeException.getCode());

    }



}
