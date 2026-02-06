package com.zj.infinitechat.offlinedatastoreservice.service;

import com.zj.infinitechat.offlinedatastoreservice.data.offlineData.OfflineMessageRequest;
import com.zj.infinitechat.offlinedatastoreservice.data.offlineData.OfflineMessageResponse;
import com.zj.infinitechat.offlinedatastoreservice.model.Message;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【message】的数据库操作Service
* @createDate 2026-02-05 19:32:39
*/
public interface MessageService extends IService<Message> {

    public OfflineMessageResponse getOfflineMessage(OfflineMessageRequest request);

    void saveMessage(String message);
}
