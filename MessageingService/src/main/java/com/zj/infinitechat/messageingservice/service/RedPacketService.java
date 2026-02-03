package com.zj.infinitechat.messageingservice.service;

import com.zj.infinitechat.messageingservice.data.RedPacket.Send.SendRedPacketRequest;
import com.zj.infinitechat.messageingservice.data.RedPacket.Send.SendRedPacketResponse;
import com.zj.infinitechat.messageingservice.model.RedPacket;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【red_packet(红包主表)】的数据库操作Service
* @createDate 2026-02-02 13:58:52
*/
public interface RedPacketService extends IService<RedPacket> {

    SendRedPacketResponse sendPacket(SendRedPacketRequest request);



    void handleExpiredRedPacket(Long redPacketId);
}
