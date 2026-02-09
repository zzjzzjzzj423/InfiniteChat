package com.zj.infinitechat.friendmomentservice.constants;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ConfigEnum {

    TOKEN_SECRET_KEY("ZZJ_STUDY_KEY"),
    PASSWORD_SALT("ZZJ"),
    WX_STATE("ZZJ"),
    WORKED_ID("1"),
    DATACENTER_ID("1"),
    IMAGE_URI("http://127.0.0.1:9000/infinitec-chat/"),
    IMAGE_PATH("/home/img/avatar"),
    NOTICE_URL("/api/v1/message/push/Moment"),
    MEDIA_TYPE("application/json; charset=utf-8"),
    MINIO_SERVER_URL("http://127.0.0.1:9000"),
    MINIO_ACCESS_KEY("minioadmin"),
    MINIO_SECRET_KEY("minioadmin"),
    REQUEST_SUCCESSFUL("请求成功"),
    MINIO_BUCKET_NAME("infinitec-chat"),
    REALTIME("RealTimeCommunicationService");

    private final String value;

    ConfigEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
