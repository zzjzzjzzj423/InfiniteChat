package com.zj.infinitechat.friendmomentservice.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.zj.infinitechat.friendmomentservice.constants.ConfigEnum;
import com.zj.infinitechat.friendmomentservice.constants.NoticeMomentEnum;
import com.zj.infinitechat.friendmomentservice.data.createMoment.CreateMomentRequest;
import com.zj.infinitechat.friendmomentservice.data.createMoment.CreateMomentResponse;
import com.zj.infinitechat.friendmomentservice.model.Friend;
import com.zj.infinitechat.friendmomentservice.model.Moment;
import com.zj.infinitechat.friendmomentservice.model.User;
import com.zj.infinitechat.friendmomentservice.service.FriendService;
import com.zj.infinitechat.friendmomentservice.service.MomentService;
import com.zj.infinitechat.friendmomentservice.mapper.MomentMapper;
import com.zj.infinitechat.friendmomentservice.service.SendService;
import com.zj.infinitechat.friendmomentservice.service.UserService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【moment(朋友圈)】的数据库操作Service实现
* @createDate 2026-02-08 18:51:57
*/
@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment>
    implements MomentService{

    private final Gson gson = new Gson();
    @Autowired
    private FriendService friendService;
    @Autowired
    private SendService sendService;
    private UserService userService;

    @Override
    public CreateMomentResponse createMoment(CreateMomentRequest request) {
        long userId = Long.parseLong(request.getUserId());
        long id = notifyFriends(request , userId);
        CreateMomentResponse createMomentResponse = new CreateMomentResponse();
        createMomentResponse.setMomentId(id).setUserId(userId).setText(request.getText()).setMediaUrls(request.getMediaUrls());
        return  createMomentResponse;

    }

    private long notifyFriends(CreateMomentRequest request , long userid){
        LambdaQueryWrapper<Friend> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Friend::getUserId , userid);
        List<Long> friendList = friendService.list(lambdaQueryWrapper).stream().map(new Function<Friend, Long>() {

            @Override
            public Long apply(Friend friend) {
                return friend.getFriendId();
            }
        }).collect(Collectors.toList());

        User user = userService.getById(userid);
        sendService.sendToUser(user , friendList , NoticeMomentEnum.CREATE_MOMENT_NOTICE.getValue());
        return saveMoment(request);

    }

    private long saveMoment(CreateMomentRequest request){
        long work_id = Long.parseLong(ConfigEnum.WORKED_ID.getValue());
        long datacenter_id = Long.parseLong(ConfigEnum.DATACENTER_ID.getValue());
        Snowflake snowflake = new Snowflake(work_id , datacenter_id);
        long movementId = snowflake.nextId();
        String url = gson.toJson(request.getMediaUrls());
        Moment savedMoment = new Moment();
        savedMoment.setMomentId(movementId);
        savedMoment.setUserId(Long.valueOf(request.getUserId()));
        savedMoment.setText(request.getText());
        savedMoment.setMediaUrl(url);
        this.save(savedMoment);
        return movementId;
    }
}




