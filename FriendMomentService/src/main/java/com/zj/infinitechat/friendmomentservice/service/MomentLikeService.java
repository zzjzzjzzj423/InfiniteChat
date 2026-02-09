package com.zj.infinitechat.friendmomentservice.service;

import com.zj.infinitechat.friendmomentservice.data.createLike.CreateLikeRequest;
import com.zj.infinitechat.friendmomentservice.data.createLike.CreateLikeResponse;
import com.zj.infinitechat.friendmomentservice.model.MomentLike;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【moment_like(朋友圈点赞)】的数据库操作Service
* @createDate 2026-02-08 18:52:02
*/
@Mapper
public interface MomentLikeService extends IService<MomentLike> {
     CreateLikeResponse likeMomentResponse(long momentId , CreateLikeRequest request);
}
