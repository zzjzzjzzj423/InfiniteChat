package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AckData {

    private Long sessionId;

    private Long receiveUserUuid;

    private String msgUuid;
}
