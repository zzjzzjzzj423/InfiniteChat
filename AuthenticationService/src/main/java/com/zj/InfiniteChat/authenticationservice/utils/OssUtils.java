package com.zj.InfiniteChat.authenticationservice.utils;


import cn.hutool.core.util.StrUtil;
import com.zj.InfiniteChat.authenticationservice.constants.OssConstants;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@Service
public class OssUtils {
    @Resource
    private MinioClient minioClient;

    @Value("{minio.url}")
    private String url;

    public String getDownloadUrl(String filename){
        return url+ StrUtil.SLASH+ OssConstants.BUCKET_NAME+StrUtil.SLASH+filename;

    }


    @SneakyThrows
    public String uploadUrl(String objectName, Integer expires) {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(OssConstants.BUCKET_NAME)
                        .object(objectName)
                        .expiry(expires, TimeUnit.SECONDS)
                        .build());
    }


}
