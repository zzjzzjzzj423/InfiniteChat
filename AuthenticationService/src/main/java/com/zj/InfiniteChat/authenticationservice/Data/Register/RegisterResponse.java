package com.zj.InfiniteChat.authenticationservice.Data.Register;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegisterResponse {
    private String email;
    private String message;
    private boolean success;

}
