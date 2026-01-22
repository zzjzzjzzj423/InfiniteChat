package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.service.Impl;

import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.data.ReceiveMessageRequest;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.data.ReceiveMessageResponse;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.service.RcvMsgServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RvcMsgServiceImpl implements RcvMsgServer {

    @Autowired
    private NettyMessageService nettyMessageService;

    @Override
    public ReceiveMessageResponse receiveMessage(ReceiveMessageRequest request) {
        nettyMessageService.sendMessageToUser(request);
        return null;
    }
}
