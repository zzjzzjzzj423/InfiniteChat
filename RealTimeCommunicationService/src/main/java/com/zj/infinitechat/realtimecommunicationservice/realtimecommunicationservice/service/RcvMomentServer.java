package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.service;

import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.data.PushMomentRequest;


public interface RcvMomentServer {

    public void sendNotification(PushMomentRequest request);

}
