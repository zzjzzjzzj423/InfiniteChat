package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.service;

import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.data.ReceiveMessageRequest;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.data.ReceiveMessageResponse;

public interface RcvMsgServer {
    public ReceiveMessageResponse receiveMessage(ReceiveMessageRequest request);
}
