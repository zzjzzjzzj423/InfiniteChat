package com.zj.infinitechat.messageingservice.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.infinitechat.messageingservice.common.ServiceException;
import com.zj.infinitechat.messageingservice.constants.BalanceLogType;
import com.zj.infinitechat.messageingservice.constants.RedPacketConstants;
import com.zj.infinitechat.messageingservice.constants.RedPacketStatus;
import com.zj.infinitechat.messageingservice.data.RedPacket.Receive.ReceiveRedPacketRequest;
import com.zj.infinitechat.messageingservice.data.RedPacket.Receive.ReceiveRedPacketResponse;
import com.zj.infinitechat.messageingservice.model.BalanceLog;
import com.zj.infinitechat.messageingservice.model.RedPacket;
import com.zj.infinitechat.messageingservice.model.RedPacketReceive;
import com.zj.infinitechat.messageingservice.model.UserBalance;
import com.zj.infinitechat.messageingservice.service.BalanceLogService;
import com.zj.infinitechat.messageingservice.service.RedPacketReceiveService;
import com.zj.infinitechat.messageingservice.mapper.RedPacketReceiveMapper;
import com.zj.infinitechat.messageingservice.service.RedPacketService;
import com.zj.infinitechat.messageingservice.service.UserBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【red_packet_receive(红包领取记录表)】的数据库操作Service实现
* @createDate 2026-02-02 13:59:04
*/
@Service
@Slf4j
public class RedPacketReceiveServiceImpl extends ServiceImpl<RedPacketReceiveMapper, RedPacketReceive>
    implements RedPacketReceiveService{

    @Autowired
    private RedPacketService redPacketService;
    @Autowired
    private UserBalanceService userBalanceService;
    @Autowired
    private BalanceLogService balanceLogService;

    @Autowired
    private RedisTemplate<String , String>  USER_LOCK;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    @Transactional
    public ReceiveRedPacketResponse receiveRedPacket(ReceiveRedPacketRequest request) {
        String key = RedPacketConstants.USER_RED_PACKET_KEY_PREFIX.getValue()+request.getRedPacketId()+":"+request.getUserId();
        boolean res = USER_LOCK.opsForValue().setIfAbsent(key , "locked" , 30 , TimeUnit.SECONDS);
        if(!res){
            throw new ServiceException("用户抢夺过快重复请求");
        }
        try{
        int exeRes = executeDecreaseCount(request.getRedPacketId());

        if(exeRes == 0){
            throw new ServiceException("没有该红包");
        }else if(exeRes == 2){
            throw new ServiceException("红包被抢完");
        }
        RedPacket redPacket = redPacketService.getById(request.getRedPacketId());
        if(redPacket.getStatus() != RedPacketStatus.UNCLAIMED.getStatus()){
            return new ReceiveRedPacketResponse(null , redPacket.getStatus());
        }
        BigDecimal amount = distributeRedPacket(redPacket);

        saveRedPacketReceive(redPacket , request.getUserId() , amount);

        saveBalanceLog(request.getUserId() , amount , redPacket , BalanceLogType.RECEIVE_RED_PACKET.getType());

        updateBalance(request.getUserId() , amount);

        updateRedPacket(redPacket , amount);
        return new ReceiveRedPacketResponse(amount , redPacket.getStatus());
    }finally {
            USER_LOCK.delete(key);
        }
    }

    private Integer executeDecreaseCount(long redPacketId){
        String key = RedPacketConstants.RED_PACKET_KEY_PREFIX.getValue() + redPacketId;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(RedPacketConstants.RED_PACKET_LUA_SCRIPT.getValue());
        redisScript.setResultType(Long.class);
        try{
            Long res = stringRedisTemplate.execute(redisScript , Collections.singletonList(key));
            if(res == null){
                throw new ServiceException("redis红包计数发生错误");
            }
            return res.intValue();
        } catch (Exception e){
            log.error("lua脚本错误");
            throw new RuntimeException("执行 Redis Lua 脚本时出错", e);
        }
    }

    private BigDecimal distributeRedPacket(RedPacket redPacket){

        if(Objects.equals(redPacket.getRedPacketType(), RedPacketConstants.RED_PACKET_TYPE_NORMAL.getIntValue())){
            return distributeNormal(redPacket);
        }else if(Objects.equals(redPacket.getRedPacketType(), RedPacketConstants.RED_PACKET_TYPE_RANDOM.getIntValue())){
            return distributeRandom(redPacket);
        }else{
            throw new ServiceException("红包类型出错");
        }

    }

    private BigDecimal distributeNormal(RedPacket redPacket){
        return redPacket.getTotalAmount()
                .divide(new BigDecimal(redPacket.getTotalCount()), RedPacketConstants.DIVIDE_SCALE.getDivideScale(), RoundingMode.DOWN);
    }

    private BigDecimal distributeRandom(RedPacket redPacket){
        if(redPacket.getRemainingCount() == 1){
            return redPacket.getRemainingAmount();
        }
        BigDecimal maxAmount = redPacket.getRemainingAmount()
                .divide(new BigDecimal(redPacket.getRemainingCount()), RedPacketConstants.DIVIDE_SCALE.getDivideScale(), RoundingMode.DOWN)
                .multiply(RedPacketConstants.RANDOM_MULTIPLIER.getBigDecimalValue());
        return generateRandomAmount(RedPacketConstants.MIN_AMOUNT.getBigDecimalValue(),
                maxAmount);


    }

    private BigDecimal generateRandomAmount(BigDecimal min , BigDecimal max){
        BigDecimal range = max.subtract(min);

        BigDecimal randomRange = range.multiply(BigDecimal.valueOf(Math.random()));

        BigDecimal randomAmount = min.add(randomRange).setScale(RedPacketConstants.AMOUNT_SCALE.getDivideScale(), RoundingMode.DOWN);

        return randomAmount.compareTo(min) < 0 ? min : randomAmount;

    }

    public void backBalance(RedPacket redPacket){
            long senderId = redPacket.getSenderId();

            UserBalance userBalance = userBalanceService.getById(senderId);

            saveBalanceLog(senderId , redPacket.getRemainingAmount() , redPacket , BalanceLogType.REFUND_RED_PACKET.getType());

            userBalance.setBalance(userBalance.getBalance().add(redPacket.getRemainingAmount()));

            userBalanceService.updateById(userBalance);

    }

    private void updateBalance(long userId , BigDecimal amount){
        UserBalance userBalance = userBalanceService.getById(userId);

        userBalance.setBalance(userBalance.getBalance().add(amount));

        userBalanceService.updateById(userBalance);
    }

    private void saveRedPacketReceive(RedPacket redPacket , long userId , BigDecimal amount){
        RedPacketReceive redPacketReceive = new RedPacketReceive();
        Snowflake snowflake = IdUtil.getSnowflake(RedPacketConstants.WORKED_ID.getIntValue()
                , RedPacketConstants.DATACENTER_ID.getIntValue());
        long receiveId = snowflake.nextId();
        Date date = new Date();
        redPacketReceive.setRedPacketReceiveId(receiveId)
                .setRedPacketId(redPacket.getRedPacketId())
                .setReceiverId(userId)
                .setAmount(amount)
                .setReceivedAt(date);
        this.save(redPacketReceive);
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

    private void updateRedPacket(RedPacket redPacket , BigDecimal amount){
        redPacket.setRemainingAmount(redPacket.getRemainingAmount().subtract(amount));
        redPacket.setRemainingCount(redPacket.getRemainingCount() - 1);

        if(redPacket.getRemainingCount() == 0){

            redPacket.setStatus(RedPacketStatus.CLAIMED.getStatus());

            if(redPacket.getRemainingAmount().compareTo(new BigDecimal(0)) > 0){
                backBalance(redPacket);
            }

        }
        redPacketService.updateById(redPacket);
    }



}




