package com.zj.infinitechat.messageingservice.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.infinitechat.messageingservice.common.ServiceException;
import com.zj.infinitechat.messageingservice.constants.ConfigEnum;
import com.zj.infinitechat.messageingservice.constants.SessionType;
import com.zj.infinitechat.messageingservice.constants.UserConstants;
import com.zj.infinitechat.messageingservice.data.sendMsg.AppMessage;
import com.zj.infinitechat.messageingservice.data.sendMsg.SendMsgRequest;
import com.zj.infinitechat.messageingservice.data.sendMsg.SendMsgResponse;
import com.zj.infinitechat.messageingservice.model.*;
import com.zj.infinitechat.messageingservice.mapper.MessageMapper;
import com.zj.infinitechat.messageingservice.service.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【message】的数据库操作Service实现
* @createDate 2026-01-28 16:06:42
*/
@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 60L; // 60秒
    private static final int QUEUE_CAPACITY = 100;
    private static final String DEFAULT_SESSION_AVATAR = "http://47.115.130.44/img/avatar/IM_GROUP.jpg";
    private static final String TIME_ZONE_SHANGHAI = "Asia/Shanghai";
    private static final int STATUS_ACTIVE = 1;
    private final OkHttpClient httpClient = new OkHttpClient();
    private final FriendService friendService;
    private final UserService userService;
    private final SessionService sessionService;
    private final UserSessionService userSessionService;
    private final ThreadPoolExecutor groupMessageExecutor;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private final DiscoveryClient discoveryClient;
    @Autowired
    public MessageServiceImpl(FriendService friendService ,
                              UserService userService ,
                              SessionService sessionService,
                              UserSessionService userSessionService,
                              DiscoveryClient discoveryClient){
        this.friendService = friendService;
        this.userService = userService;
        this.sessionService = sessionService;
        this.userSessionService = userSessionService;
        this.groupMessageExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy()

        );
        this.discoveryClient = discoveryClient;
    }


    @Override
    public SendMsgResponse sendMessage(SendMsgRequest request) throws IOException {
        if(!validate(request.getSendUserId())){
            throw new ServiceException("发送者不存在");
        }
        if(!validateRelation(request)){
            throw new ServiceException("关系认证失败");
        }
        AppMessage appMessage = constructAppMessage(request);
        transferMessage(request , appMessage);
        return toReponseVo(appMessage);
    }



    private boolean validate(Long id){
        User findUser = userService.getById(id);
        return findUser != null;
    }

    private  boolean validateRelation(SendMsgRequest request){
        if(request.getSessionType() == SessionType.GROUP.getValue()){
            LambdaQueryWrapper<UserSession> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserSession::getSessionId , request.getSessionId());
            List<UserSession> groupRelationList = userSessionService.list(lambdaQueryWrapper);
            for(UserSession item : groupRelationList){
                if(Objects.equals(item.getUserId(), request.getSendUserId())){
                    return true;
                }
            }
            return false;
        }else{
            if(!validate(request.getReceiveUserId())){
                throw new ServiceException("接受者不存在");
            }
            LambdaQueryWrapper<Friend> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Friend::getUserId , request.getSendUserId());
            List<Friend> friends = friendService.list(lambdaQueryWrapper);
            for(Friend item : friends){
                if(Objects.equals(item.getFriendId(), request.getReceiveUserId())) {
                    return true;
                }
            }
            return false;

        }
    }

    private AppMessage constructAppMessage(SendMsgRequest request){
        AppMessage appMessage = new AppMessage()
                .setSessionId(request.getSessionId())
                .setSendUserId(request.getSendUserId())
                .setSessionType(request.getSessionType())
                .setType(request.getType())
                .setBody(request.getBody());
        List<Long> receiveUserIds = new ArrayList<>();
        Snowflake snowId = IdUtil.getSnowflake(1,1);
        appMessage.setMessageId(snowId.nextId());
        if(request.getSessionType() == SessionType.GROUP.getValue()){
            LambdaQueryWrapper<UserSession> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserSession::getSessionId , request.getSessionId());
            List<UserSession> memberList = userSessionService.list(lambdaQueryWrapper);
            for(UserSession item : memberList){
                if(Objects.equals(item.getId(), request.getSendUserId())){
                    continue;
                }
                receiveUserIds.add(item.getUserId());
            }
            Session selectedSession = sessionService.getById(request.getSessionId());
            appMessage.setSessionAvatar(DEFAULT_SESSION_AVATAR);
            appMessage.setSessionName(selectedSession.getName());

        }else{
            receiveUserIds.add(request.getReceiveUserId());

            User selectedUser = userService.getById(request.getSendUserId());
            appMessage.setAvatar(selectedUser.getAvatar());
            appMessage.setUserName(selectedUser.getUserName());
            appMessage.setSessionAvatar(null);
            appMessage.setSessionName(null);

        }
        appMessage.setReceiveUserIds(receiveUserIds);
        Date createdAt = new Date();
        appMessage.setCreatedAt(formatDate(createdAt));
        return appMessage;
    }

    private void executeHttpRequest(Request request) throws IOException{
        try(Response response = httpClient.newCall(request).execute()){
            if(!response.isSuccessful()){
                throw new IOException("HTTP请求失败: " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseString = responseBody.string();
                // 处理响应内容（根据业务需求）
                log.info("HTTP响应: {}", responseString);
            }

        }
    }

    private void sendGroupMessage(List<ServiceInstance> instances, RequestBody requestBody, String token){
        for(ServiceInstance instance : instances){
            groupMessageExecutor.submit(()->{
                String url = instance.getUri().toString();
                Request request = new Request.Builder()
                        .url(url + ConfigEnum.MSG_URL.getValue())
                        .post(requestBody)
                        .addHeader("Authorization" , token)
                        .build();
                try {
                    executeHttpRequest(request);
                    log.info("成功发送群聊消息到 {}", url);
                } catch (Exception e) {
                    log.error("发送群聊消息到 {} 失败: {}", url, e.getMessage());
                    // 根据需求，可以在此处添加重试机制或其他错误处理逻辑
                }
            });
        }
    }

    private void sendSingleMessage(SendMsgRequest sendMsgRequest, RequestBody requestBody, String nettyServerIP) {
        String receiveUserId = String.valueOf(sendMsgRequest.getReceiveUserId());
        try {
            if (nettyServerIP != null) {
                Request request = new Request.Builder()
                        .url("http://" + nettyServerIP + ":8083" + ConfigEnum.MSG_URL.getValue())
                        .post(requestBody)
                        .build();
                executeHttpRequest(request);
            } else {
                log.info("接收者已下线: {}", receiveUserId);
            }
        } catch (Exception e) {
            log.error("发送单聊消息失败: {}", e.getMessage());
            throw new ServiceException("发送单聊消息失败");
        }
    }

    private void transferMessage(SendMsgRequest request , AppMessage appMessage) throws IOException {
        String json = JSON.toJSONString(appMessage);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse(ConfigEnum.MEDIA_TYPE.getValue()),
                json
        );
        String receiveIP = stringRedisTemplate.opsForValue().get(UserConstants.USER_SESSION+request.getReceiveUserId());

        if(SessionType.GROUP.getValue() == request.getSessionType()){
            List<ServiceInstance> instances = discoveryClient.getInstances("RealTimeCommunicationService");
            if (instances.isEmpty()) {
                throw new ServiceException("没有可用的RealTimeCommunicationService服务实例");
            }
            sendGroupMessage(instances , requestBody , receiveIP);
        }else{
            sendSingleMessage(request , requestBody , receiveIP);
        }
    }

    private SendMsgResponse toReponseVo(AppMessage appMessage){
        SendMsgResponse responseMsgVo = new SendMsgResponse();
        BeanUtils.copyProperties(appMessage, responseMsgVo);
        responseMsgVo.setSessionId(String.valueOf(appMessage.getSessionId()));
        responseMsgVo.setCreatedAt(appMessage.getCreatedAt());
        return responseMsgVo;
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_SHANGHAI));
        return formatter.format(date);
    }
}




