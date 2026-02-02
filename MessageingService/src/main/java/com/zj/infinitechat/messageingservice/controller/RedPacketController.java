package com.zj.infinitechat.messageingservice.controller;

import com.zj.infinitechat.messageingservice.data.RedPacket.Get.RedPacketResponse;
import com.zj.infinitechat.messageingservice.data.RedPacket.Send.SendRedPacketRequest;
import com.zj.infinitechat.messageingservice.data.RedPacket.Send.SendRedPacketResponse;
import com.zj.infinitechat.messageingservice.service.RedPacketService;
import com.zj.infinitechat.messageingservice.util.PreventDuplicateSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/RedPacketContact")
public class RedPacketController {

    @Autowired
    private RedPacketService redPacketService;

    @PreventDuplicateSubmit
    @PostMapping("/Send")
    public SendRedPacketResponse sendRedPacket(@RequestBody SendRedPacketRequest sendRedPacketRequest){

        return redPacketService.sendPacket(sendRedPacketRequest);
    }

    @GetMapping("/GetInfo/{redPacketId}")
    public RedPacketResponse getRedPacketInfo(@PathVariable long redPacketId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int pageSize){


    }
}
