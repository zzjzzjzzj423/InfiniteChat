package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.controller;


import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.common.Result;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.data.ReceiveMessageRequest;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.data.ReceiveMessageResponse;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.service.RcvMsgServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/message")
@Slf4j
@RequiredArgsConstructor
public class RcvMsgController {

    @Autowired
    private RcvMsgServer rcvMsgServer;

    @PostMapping("/user")
    public Result<ReceiveMessageResponse> receiveMessage(@RequestBody ReceiveMessageRequest request){
        ReceiveMessageResponse response = rcvMsgServer.receiveMessage(request);

        return Result.OK(response);
    }
}
