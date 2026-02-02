package com.zj.infinitechat.messageingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.infinitechat.messageingservice.data.RedPacket.Send.SendRedPacketRequest;
import com.zj.infinitechat.messageingservice.model.UserBalance;
import com.zj.infinitechat.messageingservice.service.UserBalanceService;
import com.zj.infinitechat.messageingservice.mapper.UserBalanceMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
* @author Administrator
* @description 针对表【user_balance(用户余额表)】的数据库操作Service实现
* @createDate 2026-02-02 13:59:13
*/
@Service
public class UserBalanceServiceImpl extends ServiceImpl<UserBalanceMapper, UserBalance>
    implements UserBalanceService{

    @Override
    public void updateBalance(BigDecimal balance, SendRedPacketRequest request) {
        long userid = request.getSendUserId();

        UserBalance userBalance = this.getById(userid);
        userBalance.setBalance(balance);
        this.updateById(userBalance);
    }
}




