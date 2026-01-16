package com.zj.InfiniteChat.authenticationservice.Data.Common.Avatar.Upload;


import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UploadRequest {

    @NonNull
    private String filename;


}
