package com.zj.InfiniteChat.authenticationservice.Data.Common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommonResponse {
    private String email;

}
