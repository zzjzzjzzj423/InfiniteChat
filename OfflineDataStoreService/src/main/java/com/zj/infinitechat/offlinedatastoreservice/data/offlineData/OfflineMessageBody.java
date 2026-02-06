package com.zj.infinitechat.offlinedatastoreservice.data.offlineData;

import lombok.Data;

import java.io.Serializable;

@Data
public class OfflineMessageBody implements Serializable {
    private String content;

    private String createdAt;

    private String replyId;
}
