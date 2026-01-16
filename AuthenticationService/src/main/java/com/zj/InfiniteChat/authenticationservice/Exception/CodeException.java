package com.zj.InfiniteChat.authenticationservice.Exception;

import com.zj.InfiniteChat.authenticationservice.constants.Enum.ERROR_ENUMS;
import lombok.Data;

@Data
public class CodeException extends RuntimeException {

    private final int code;

    public CodeException(String message) {
        super(message);
        code=ERROR_ENUMS.CODE_ERROR.getCode();
    }

    public CodeException(ERROR_ENUMS errorEnums){
        super(errorEnums.getMessage());
        code=errorEnums.getCode();
    }


    public CodeException(ERROR_ENUMS errorEnums, String message){
        super(message);
        code=errorEnums.getCode();
    }

}
