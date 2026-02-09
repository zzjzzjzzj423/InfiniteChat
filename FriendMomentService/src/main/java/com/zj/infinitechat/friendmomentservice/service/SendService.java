package com.zj.infinitechat.friendmomentservice.service;

import com.zj.infinitechat.friendmomentservice.model.User;

import java.util.List;

public interface SendService {
    void sendToUser(User user , List<Long> friends_list , Integer noticeType);
}
