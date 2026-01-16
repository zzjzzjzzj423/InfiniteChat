package com.zj.InfiniteChat.authenticationservice.commom;


import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Data
@Accessors(chain = true)
public class Result <T> {

    private T data;
    private int code;
    private String message;


    public static<T> Result<T> ok(T data){
        return new Result<T>().setCode(HttpStatus.OK.value()).setData(data);
    }

    public static<T> Result<T> errorServer(T data){

        return new Result<T>().setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static<T> Result<T> errorUser(T data){
        return new Result<T>().setCode(HttpStatus.NOT_ACCEPTABLE.value());

    }

}
