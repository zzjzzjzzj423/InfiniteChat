package com.zj.infinitechat.messageingservice.controller;


import com.zj.infinitechat.messageingservice.common.Result;
import com.zj.infinitechat.messageingservice.data.sendMsg.SendMsgRequest;
import com.zj.infinitechat.messageingservice.data.sendMsg.SendMsgResponse;
import com.zj.infinitechat.messageingservice.feign.ContactServiceFeign;
import com.zj.infinitechat.messageingservice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/contact")
public class ContactController {
    @Autowired
    private ContactServiceFeign contactServiceFeign;
    @Autowired
    private MessageService messageService;

    @GetMapping("/feign")
    public Result<?> feignTest(){

        Result<?> user = contactServiceFeign.getUser();

        return Result.OK(user);
    }

    @PostMapping("/v1/chat/session")
    public Result<SendMsgResponse> sendMsg(@RequestBody SendMsgRequest request) throws Exception {
        SendMsgResponse response = messageService.sendMessage(request);

        return Result.OK(response);
    }

}
