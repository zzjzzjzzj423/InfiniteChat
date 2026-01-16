package com.zj.InfiniteChat.authenticationservice.Data.Login;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginCodeRequest {

    private String email;
    private String code;

}
