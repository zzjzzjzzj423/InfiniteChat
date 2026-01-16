package com.zj.InfiniteChat.authenticationservice.Exception;


import com.zj.InfiniteChat.authenticationservice.constants.Enum.ERROR_ENUMS;
import lombok.Data;

@Data
public class UserException extends RuntimeException{

    private final int code;

    public UserException(String message){
        super(message);
        this.code = ERROR_ENUMS.USER_ERROR.getCode();
    }

    public UserException(ERROR_ENUMS errorEnums){
        super(errorEnums.getMessage());
        this.code = errorEnums.getCode();

    }

    public UserException(ERROR_ENUMS errorEnums, String message){
        super(message);
        this.code=errorEnums.getCode();
    }


}
