package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.service.Impl;

import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.data.PushMomentRequest;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.service.RcvMomentServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RcvMomentServerImpl implements RcvMomentServer {

    @Autowired
    private NettyMessageService nettyMessageService;

    @Override
    public void sendNotification(PushMomentRequest request) {
        nettyMessageService.sendNotification(request);
    }
}
