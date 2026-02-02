package com.zj.infinitechat.messageingservice.service;

import com.zj.infinitechat.messageingservice.data.RedPacket.Send.SendRedPacketRequest;
import com.zj.infinitechat.messageingservice.model.UserBalance;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
* @author Administrator
* @description 针对表【user_balance(用户余额表)】的数据库操作Service
* @createDate 2026-02-02 13:59:13
*/
public interface UserBalanceService extends IService<UserBalance> {

    public void updateBalance(BigDecimal balance , SendRedPacketRequest request);

}
