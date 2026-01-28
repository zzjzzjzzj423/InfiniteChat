package com.zj.infinitechat.messageingservice.service;

import com.zj.infinitechat.messageingservice.data.sendMsg.SendMsgRequest;
import com.zj.infinitechat.messageingservice.data.sendMsg.SendMsgResponse;
import com.zj.infinitechat.messageingservice.model.Message;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
* @author Administrator
* @description 针对表【message】的数据库操作Service
* @createDate 2026-01-28 16:06:42
*/
public interface MessageService extends IService<Message> {
    SendMsgResponse sendMessage(SendMsgRequest request) throws IOException;

}
