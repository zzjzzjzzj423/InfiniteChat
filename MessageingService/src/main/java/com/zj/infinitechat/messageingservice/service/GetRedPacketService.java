package com.zj.infinitechat.messageingservice.service;

import com.zj.infinitechat.messageingservice.data.RedPacket.Get.RedPacketResponse;
import com.zj.infinitechat.messageingservice.data.RedPacket.Receive.RedPacketUser;
import com.zj.infinitechat.messageingservice.mapper.RedPacketReceiveMapper;
import com.zj.infinitechat.messageingservice.model.RedPacket;
import com.zj.infinitechat.messageingservice.model.RedPacketReceive;
import com.zj.infinitechat.messageingservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GetRedPacketService {

    @Autowired
    private UserService userService;
    @Autowired
    private RedPacketService redPacketService;
    @Autowired
    private RedPacketReceiveMapper redPacketReceiveMapper;
    @Autowired
    private RedPacketReceiveService redPacketReceiveService;
    public RedPacketResponse getRedPacketInfo(long redPacketId , int page , int pageSize){
        RedPacket redPacket = redPacketService.getById(redPacketId);

        List<RedPacketReceive> receiveList =  redPacketReceiveMapper.selectByRedPacketId(redPacketId , page, pageSize);
        User sender = userService.getById(redPacket.getSenderId());
        RedPacketResponse response = new RedPacketResponse(getUserList(receiveList) ,
                sender.getUserName() , sender.getAvatar() , redPacket.getRedPacketWrapperText() , redPacket.getRedPacketType() ,
                redPacket.getTotalAmount() , redPacket.getTotalCount() , redPacket.getRemainingAmount() , redPacket.getRemainingCount()
        ,redPacket.getStatus());
        return response;





    }

    private List<RedPacketUser> getUserList(List<RedPacketReceive> list){
        List<RedPacketUser> ansList = new ArrayList<>();
        for(RedPacketReceive item : list){
            long receiverId = item.getReceiverId();
            User receiveUser = userService.getById(receiverId);
            ansList.add(new RedPacketUser(receiveUser.getUserName() , receiveUser.getAvatar() , String.valueOf(item.getReceivedAt()) , item.getAmount()));
        }
        return ansList;
    }

}
