package com.zj.InfiniteChat.authenticationservice.Data.Register;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String code;
    private String password;
    private String phone;
}
