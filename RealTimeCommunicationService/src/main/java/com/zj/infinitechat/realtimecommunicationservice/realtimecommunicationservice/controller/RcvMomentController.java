package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.controller;


import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.data.PushMomentRequest;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.service.Impl.NettyMessageService;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.service.RcvMomentServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/message/push")
@RestController
public class RcvMomentController {

    @Autowired
    private RcvMomentServer rcvMomentServer;

    @GetMapping("/Moment")
    public void transferMomentNotification(@RequestBody PushMomentRequest request){
        rcvMomentServer.sendNotification(request);

    }

}
