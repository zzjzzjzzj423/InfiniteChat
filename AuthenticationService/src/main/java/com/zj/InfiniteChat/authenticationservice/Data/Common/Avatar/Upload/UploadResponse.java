package com.zj.InfiniteChat.authenticationservice.Data.Common.Avatar.Upload;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UploadResponse {
    private String downloadUrl;
    private String uploadUrl;
}
