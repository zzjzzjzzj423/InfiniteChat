package com.zj.infinitechat.messageingservice.service;

import com.zj.infinitechat.messageingservice.data.RedPacket.Receive.ReceiveRedPacketRequest;
import com.zj.infinitechat.messageingservice.data.RedPacket.Receive.ReceiveRedPacketResponse;
import com.zj.infinitechat.messageingservice.model.RedPacket;
import com.zj.infinitechat.messageingservice.model.RedPacketReceive;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【red_packet_receive(红包领取记录表)】的数据库操作Service
* @createDate 2026-02-02 13:59:04
*/
public interface RedPacketReceiveService extends IService<RedPacketReceive> {

    ReceiveRedPacketResponse receiveRedPacket(ReceiveRedPacketRequest request);

    public void backBalance(RedPacket redPacket);
}
