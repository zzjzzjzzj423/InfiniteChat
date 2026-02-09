package com.zj.infinitechat.friendmomentservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.zj.infinitechat.friendmomentservice.Exception.MessageSendFailureException;
import com.zj.infinitechat.friendmomentservice.Exception.ServiceUnavailableException;
import com.zj.infinitechat.friendmomentservice.constants.ConfigEnum;
import com.zj.infinitechat.friendmomentservice.constants.ErrorEnum;
import com.zj.infinitechat.friendmomentservice.constants.NoticeMomentEnum;
import com.zj.infinitechat.friendmomentservice.model.User;
import com.zj.infinitechat.friendmomentservice.model.vo.MomentRTCVO;
import com.zj.infinitechat.friendmomentservice.service.SendService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class sendServiceImpl implements SendService {


    private DiscoveryClient discoveryClient;
    private static final int DEFAULT_THREAD_POOL_SIZE = 5;
    @Override
    public void sendToUser(User user , List<Long> friends_list, Integer noticeType) {
        List<ServiceInstance> instances = findService(ConfigEnum.REALTIME.getValue());
        MomentRTCVO vo = new MomentRTCVO()
                .setAvatar(user.getAvatar())
                .setNoticeType(NoticeMomentEnum.CREATE_MOMENT_NOTICE.getValue())
                .setReceiveUserIds(friends_list)
                .setTotal(friends_list.size());

        sendToInstances(instances , vo);

    }

    private List<ServiceInstance> findService(String serviceName){
        List<ServiceInstance> list = discoveryClient.getInstances(serviceName);
        if(list.isEmpty()){
            throw new ServiceUnavailableException("未找到该模块服务");
        }
        return list;
    }

    private void sendToInstances(List<ServiceInstance> list , MomentRTCVO vo){
        ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.get(ConfigEnum.MEDIA_TYPE.getValue());
        String bodyJson = JSON.toJSONString(vo);
        RequestBody body = RequestBody.create(mediaType , bodyJson);

        for(ServiceInstance instance : list){
            executorService.submit(()->{
                sendRequestToInstance(instance , client , body , bodyJson);
            });

        }

    }
    private void sendRequestToInstance(ServiceInstance instance, OkHttpClient client,
                                       RequestBody requestBody, String originalJson) {
        try {
            // 构建请求
            String url = instance.getUri().toString() + ConfigEnum.NOTICE_URL.getValue();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
//                    .addHeader("Authorization", token)
                    .build();

            // 执行请求
            client.newCall(request).execute();
            log.debug("成功向实例 {} 发送通知", instance.getUri());

        } catch (Exception e) {
            log.error("向实例 {} 发送通知失败: {}", instance.getUri(), e.getMessage());

            throw new MessageSendFailureException(
                    ErrorEnum.MESSAGE_SEND_FAILURE,
                    originalJson,
                    e
            );
        }
    }

}
