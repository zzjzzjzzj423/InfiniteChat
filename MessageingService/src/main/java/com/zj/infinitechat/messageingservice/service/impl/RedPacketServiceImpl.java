package com.zj.infinitechat.messageingservice.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.infinitechat.messageingservice.common.ServiceException;
import com.zj.infinitechat.messageingservice.constants.BalanceLogType;
import com.zj.infinitechat.messageingservice.constants.RedPacketConstants;
import com.zj.infinitechat.messageingservice.constants.RedPacketStatus;
import com.zj.infinitechat.messageingservice.data.RedPacket.Send.RedPacketMessageBody;
import com.zj.infinitechat.messageingservice.data.RedPacket.Send.SendRedPacketRequest;
import com.zj.infinitechat.messageingservice.data.RedPacket.Send.SendRedPacketResponse;
import com.zj.infinitechat.messageingservice.data.sendMsg.SendMsgRequest;
import com.zj.infinitechat.messageingservice.data.sendMsg.SendMsgResponse;
import com.zj.infinitechat.messageingservice.model.BalanceLog;
import com.zj.infinitechat.messageingservice.model.RedPacket;
import com.zj.infinitechat.messageingservice.model.UserBalance;
import com.zj.infinitechat.messageingservice.service.*;
import com.zj.infinitechat.messageingservice.mapper.RedPacketMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
* @author Administrator
* @description 针对表【red_packet(红包主表)】的数据库操作Service实现
* @createDate 2026-02-02 13:58:52
*/
@Service
public class RedPacketServiceImpl extends ServiceImpl<RedPacketMapper, RedPacket>
    implements RedPacketService {

    @Autowired
    private UserBalanceService userBalanceService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private BalanceLogService balanceLogService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Transactional
    @Override
    public SendRedPacketResponse sendPacket(SendRedPacketRequest request) {
        SendRedPacketRequest.Body requestBody = request.getBody();
        validationRequestBody(requestBody);
        BigDecimal balance = checkUser(request);
        RedPacket redPacket = saveRedPacketProcess(request);
        SendRedPacketResponse response = sendRedPacketProcess(request, redPacket);
        userBalanceService.updateBalance(balance, request);
        saveLog(request , redPacket);
        saveInRedis(redPacket);
        return response;
    }

    @Override
    public void handleExpiredRedPacket(Long redPacketId) {
        RedPacket redPacket = this.getById(redPacketId);
        redPacket.setStatus(RedPacketStatus.EXPIRED.getStatus());
        this.updateById(redPacket);
        if(redPacket.getRemainingAmount().compareTo(new BigDecimal(0)) > 0){
            backBalance(redPacket);

        }

    }


    public void backBalance(RedPacket redPacket){
        long senderId = redPacket.getSenderId();

        UserBalance userBalance = userBalanceService.getById(senderId);

        saveBalanceLog(senderId , redPacket.getRemainingAmount() , redPacket , BalanceLogType.REFUND_RED_PACKET.getType());

        userBalance.setBalance(userBalance.getBalance().add(redPacket.getRemainingAmount()));

        userBalanceService.updateById(userBalance);

    }

    private void saveBalanceLog(long userId , BigDecimal amount , RedPacket redPacket , Integer type){
        Snowflake snowflake = IdUtil.getSnowflake(RedPacketConstants.WORKED_ID.getIntValue()
                , RedPacketConstants.DATACENTER_ID.getIntValue());
        long balanceLogId = snowflake.nextId();
        BalanceLog log  = new BalanceLog();
        Date date = new Date();
        log.setBalanceLogId(balanceLogId)
                .setUserId(userId)
                .setAmount(amount)
                .setType(type)
                .setRelatedId(redPacket.getRedPacketId())
                .setCreatedAt(date);
        balanceLogService.save(log);
    }



    private void validationRequestBody(SendRedPacketRequest.Body body) {
        if (body == null) {
            throw new ServiceException("红包请求体为空");
        }
        if (!body.getRedPacketType().equals(RedPacketConstants.RED_PACKET_TYPE_NORMAL.getIntValue()) &&
                !body.getRedPacketType().equals(RedPacketConstants.RED_PACKET_TYPE_RANDOM.getIntValue())) {
            throw new ServiceException("未知红包类型");
        }

        if (body.getTotalAmount().compareTo(RedPacketConstants.MAX_AMOUNT_PER_PACKET.getBigDecimalValue()) > 0) {
            throw new ServiceException("超过200元最大限制");
        } else if (body.getTotalAmount().compareTo(RedPacketConstants.MIN_AMOUNT.getBigDecimalValue()) < 0) {
            throw new ServiceException("红包不得小于0.01元");
        }
        if (body.getTotalCount() < 1) {
            throw new ServiceException("红包个数不得小于1");
        }
    }

    private BigDecimal checkUser(SendRedPacketRequest request) {
        long sender = request.getSendUserId();
        LambdaQueryWrapper<UserBalance> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBalance::getUserId, sender);
        UserBalance userBalance = userBalanceService.getOne(lambdaQueryWrapper);
        if (userBalance.getBalance().compareTo(request.getBody().getTotalAmount()) < 0) {
            throw new ServiceException("用户余额不足无法发送红包");
        }
        return userBalance.getBalance().subtract(request.getBody().getTotalAmount());
    }


    private RedPacket saveRedPacketProcess(SendRedPacketRequest request) {
        Snowflake snowflake = IdUtil.getSnowflake(RedPacketConstants.WORKED_ID.getIntValue()
                , RedPacketConstants.DATACENTER_ID.getIntValue());
        long rpID = snowflake.nextId();
        RedPacket redPacket = new RedPacket();
        redPacket.setRedPacketId(rpID);
        redPacket.setSenderId(request.getSendUserId());
        redPacket.setRedPacketWrapperText(request.getBody().getRedPacketWrapperText());
        redPacket.setSessionId(request.getSessionId());
        redPacket.setRedPacketType(request.getBody().getRedPacketType());
        redPacket.setTotalAmount(request.getBody().getTotalAmount());
        redPacket.setTotalCount(request.getBody().getTotalCount());
        redPacket.setRemainingAmount(request.getBody().getTotalAmount());
        redPacket.setRemainingCount(request.getBody().getTotalCount());
        redPacket.setStatus(1);
        Date date = new Date();
        redPacket.setCreatedAt(date);
        this.save(redPacket);
        return redPacket;
    }

    private SendRedPacketResponse sendRedPacketProcess(SendRedPacketRequest request, RedPacket redPacket) {
        SendMsgRequest msgRequest = new SendMsgRequest();
        BeanUtils.copyProperties(request, msgRequest);
        RedPacketMessageBody packetBody = new RedPacketMessageBody();
        packetBody.setRedPacketWrapperText(redPacket.getRedPacketWrapperText());
        packetBody.setContent(String.valueOf(redPacket.getRedPacketId()));
        try {
            msgRequest.setBody(packetBody);
            SendMsgResponse response = messageService.sendMessage(msgRequest);
            SendRedPacketResponse sendRedPacketResponse = new SendRedPacketResponse();
            BeanUtils.copyProperties(response, sendRedPacketResponse);
            return sendRedPacketResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveLog(SendRedPacketRequest request, RedPacket redPacket) {
        Snowflake snowflake = IdUtil.getSnowflake(RedPacketConstants.WORKED_ID.getIntValue()
                , RedPacketConstants.DATACENTER_ID.getIntValue());
        long logid = snowflake.nextId();
        BalanceLog log = new BalanceLog();
        log.setBalanceLogId(logid);
        log.setUserId(request.getSendUserId());
        log.setAmount(request.getBody().getTotalAmount().multiply(BigDecimal.valueOf(-1)));
        log.setType(BalanceLogType.SEND_RED_PACKET.getType());
        log.setRelatedId(redPacket.getRedPacketId());
        Date date = new Date();
        log.setCreatedAt(date);
        balanceLogService.save(log);
    }

    private void saveInRedis(RedPacket redPacket){
        String key = RedPacketConstants.RED_PACKET_KEY_PREFIX.getValue() + redPacket.getRedPacketId();
        redisTemplate.opsForValue().set(key , String.valueOf(redPacket.getRemainingCount()));
    }
}




