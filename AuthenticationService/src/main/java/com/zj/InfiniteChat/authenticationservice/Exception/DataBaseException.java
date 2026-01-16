package com.zj.InfiniteChat.authenticationservice.Exception;

import com.zj.InfiniteChat.authenticationservice.constants.Enum.ERROR_ENUMS;

public class DataBaseException extends RuntimeException {
    private final int code;

    public DataBaseException(String message) {
        super(message);
        this.code= ERROR_ENUMS.DATABASE_ERROR.getCode();
    }


    public DataBaseException(ERROR_ENUMS errorEnums) {
        super(errorEnums.getMessage());
        this.code= errorEnums.getCode();
    }

    public DataBaseException(ERROR_ENUMS errorEnums,String message) {
        super(message);
        this.code= errorEnums.getCode();
    }




}
