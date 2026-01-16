package com.zj.InfiniteChat.authenticationservice.Data.Common.Avatar.Update;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class UpdateAvatarRequest {
    @NotBlank
    private String avatarUrl;

}
