package com.zj.infinitechat.offlinedatastoreservice.data.messageData;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TextMessageBody {

    private String content;

    private String replyId;
}
