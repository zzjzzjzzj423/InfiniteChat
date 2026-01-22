package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.exception;
public class BaseException extends RuntimeException{
    public BaseException(){}

    public BaseException(String msg){
        super(msg);
    }
}