package com.zj.infinitechat.offlinedatastoreservice.consumer;

import com.zj.infinitechat.offlinedatastoreservice.constants.kafka.KafkaConstants;
import com.zj.infinitechat.offlinedatastoreservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {
    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = KafkaConstants.topic , groupId = KafkaConstants.consumerGroupId)
    public void listen(String message){
        log.info("kafka收到消息");
        log.info("message : {}" , message);
        messageService.saveMessage(message);
    }


}
