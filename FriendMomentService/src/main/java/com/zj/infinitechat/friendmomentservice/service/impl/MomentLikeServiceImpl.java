package com.zj.infinitechat.friendmomentservice.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.infinitechat.friendmomentservice.constants.ConfigEnum;
import com.zj.infinitechat.friendmomentservice.constants.NoticeMomentEnum;
import com.zj.infinitechat.friendmomentservice.data.createLike.CreateLikeRequest;
import com.zj.infinitechat.friendmomentservice.data.createLike.CreateLikeResponse;
import com.zj.infinitechat.friendmomentservice.model.MomentLike;
import com.zj.infinitechat.friendmomentservice.model.User;
import com.zj.infinitechat.friendmomentservice.service.MomentLikeService;
import com.zj.infinitechat.friendmomentservice.mapper.MomentLikeMapper;
import com.zj.infinitechat.friendmomentservice.service.SendService;
import com.zj.infinitechat.friendmomentservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【moment_like(朋友圈点赞)】的数据库操作Service实现
* @createDate 2026-02-08 18:52:02
*/
@Service
public class MomentLikeServiceImpl extends ServiceImpl<MomentLikeMapper, MomentLike>
    implements MomentLikeService{

    @Autowired
    private SendService sendService;
    @Autowired
    private UserService userService;

    @Override
    public CreateLikeResponse likeMomentResponse(long momentId, CreateLikeRequest request) {



        long id = saveLike(momentId , request);
        notify(request, momentId);
        CreateLikeResponse response = new CreateLikeResponse().setLikeId(id);
        return response;
    }

    private void notify(CreateLikeRequest request , long momentId){
        List<Long> userList = findLikeUserList(momentId ,request.getUserId());
        User user = userService.getById(request.getUserId());
        sendService.sendToUser(user , userList , NoticeMomentEnum.CREATE_MOMENT_COMMENT_LIKE_NOTICE.getValue());

    }

    private List<Long> findLikeUserList(Long momentId , Long userId){
        LambdaQueryWrapper<MomentLike> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MomentLike::getMomentId , momentId);

        List<Long> list = this.list(lambdaQueryWrapper).stream().filter(new Predicate<MomentLike>() {
            @Override
            public boolean test(MomentLike momentLike) {
                return !Objects.equals(momentLike.getUserId(), userId);
            }
        }).map(new Function<MomentLike, Long>() {
            @Override
            public Long apply(MomentLike momentLike) {

                return momentLike.getUserId();
            }
        }).collect(Collectors.toList());

        return list;


    }

    private long saveLike(long momentId , CreateLikeRequest request){
        long work_id = Long.parseLong(ConfigEnum.WORKED_ID.getValue());
        long datacenter_id = Long.parseLong(ConfigEnum.DATACENTER_ID.getValue());
        Snowflake snowflake = new Snowflake(work_id , datacenter_id);
        long likeId = snowflake.nextId();
        MomentLike saveLikeRecord = new MomentLike();
        saveLikeRecord.setMomentId(likeId);
        saveLikeRecord.setMomentId(momentId);
        saveLikeRecord.setUserId(request.getUserId());
        saveLikeRecord.setCreateTime(new Date());
        saveLikeRecord.setUpdateTime(new Date());
        saveLikeRecord.setIsDelete(0);
        return likeId;

    }
}




