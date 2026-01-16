package com.zj.InfiniteChat.authenticationservice.Data.Login;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginRequest {

    private String email;

    private String password;


}
