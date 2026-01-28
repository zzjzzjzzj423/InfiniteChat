package com.zj.infinitechat.messageingservice.data.sendMsg;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class AppMessage {

    protected Long sessionId;

    protected Long sendUserId;

    protected Integer sessionType;

    protected List<Long> receiveUserIds;

    protected Integer type;

    protected Object body;

    protected String userName;

    protected String avatar;

    protected Long messageId;

    protected String sessionName;

    protected String sessionAvatar;

    private String createdAt;


}
