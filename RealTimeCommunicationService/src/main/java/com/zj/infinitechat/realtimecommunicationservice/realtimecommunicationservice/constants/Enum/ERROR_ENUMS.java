package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.constants.Enum;

import lombok.Getter;

@Getter
public enum ERROR_ENUMS {

    USER_ERROR(40000, "用户异常"),
    REGISTER_ERROR(40002,"已有该用户"),
    CODE_ERROR(40001,"验证码错误"),
    USER_NOT_EXIST(40002,"没有该用户"),
    PASSWORD_NOT_TRUE(40003,"密码错误"),
    LOGIN_CODE_ERROR(40004,"验证码错误"),
    SERVER_ERROR(50000, "服务器错误"),
    REDISCODE_ERROR(50001,"redis验证码错误"),
    DATABASE_ERROR(50002,"数据库出错");


    private final int code;
    private final String message;


    ERROR_ENUMS(int code, String message){
        this.code=code;
        this.message=message;


    }
}
