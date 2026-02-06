package com.zj.infinitechat.offlinedatastoreservice.data.messageData;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class KafkaMsgVO {

    protected Long sessionId; // app

    protected Long sendUserId; // app

    protected Long messageId; // app

    protected Integer sessionType; // app

    protected Integer type; // app

    protected String messageUuid;

    protected Date createAt; //app

    protected Object body;
}
