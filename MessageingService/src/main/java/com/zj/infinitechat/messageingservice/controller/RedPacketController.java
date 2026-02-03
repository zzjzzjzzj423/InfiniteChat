package com.zj.infinitechat.messageingservice.controller;

import com.zj.infinitechat.messageingservice.common.Result;
import com.zj.infinitechat.messageingservice.data.RedPacket.Get.RedPacketResponse;
import com.zj.infinitechat.messageingservice.data.RedPacket.Receive.ReceiveRedPacketRequest;
import com.zj.infinitechat.messageingservice.data.RedPacket.Receive.ReceiveRedPacketResponse;
import com.zj.infinitechat.messageingservice.data.RedPacket.Send.SendRedPacketRequest;
import com.zj.infinitechat.messageingservice.data.RedPacket.Send.SendRedPacketResponse;
import com.zj.infinitechat.messageingservice.service.GetRedPacketService;
import com.zj.infinitechat.messageingservice.service.RedPacketReceiveService;
import com.zj.infinitechat.messageingservice.service.RedPacketService;
import com.zj.infinitechat.messageingservice.util.PreventDuplicateSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/RedPacketContact")
public class RedPacketController {

    @Autowired
    private RedPacketService redPacketService;
    @Autowired
    private GetRedPacketService getRedPacketService;
    @Autowired
    private RedPacketReceiveService redPacketReceiveService;

    @PreventDuplicateSubmit
    @PostMapping("/Send")
    public Result<SendRedPacketResponse> sendRedPacket(@RequestBody SendRedPacketRequest sendRedPacketRequest){

        SendRedPacketResponse response = redPacketService.sendPacket(sendRedPacketRequest);
        return Result.OK(response);
    }

    @GetMapping("/GetInfo/{redPacketId}")
    public Result<RedPacketResponse> getRedPacketInfo(@PathVariable long redPacketId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int pageSize){
        RedPacketResponse response = getRedPacketService.getRedPacketInfo(redPacketId , page , pageSize);

        return Result.OK(response);
    }
    @PostMapping("/Receive")
    public Result<ReceiveRedPacketResponse> receiveRedPacket(@RequestBody ReceiveRedPacketRequest request){

        ReceiveRedPacketResponse response = redPacketReceiveService.receiveRedPacket(request);

        return Result.OK(response);
    }

}
