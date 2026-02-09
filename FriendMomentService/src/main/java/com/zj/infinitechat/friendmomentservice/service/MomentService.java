package com.zj.infinitechat.friendmomentservice.service;

import com.zj.infinitechat.friendmomentservice.data.createMoment.CreateMomentRequest;
import com.zj.infinitechat.friendmomentservice.data.createMoment.CreateMomentResponse;
import com.zj.infinitechat.friendmomentservice.model.Moment;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【moment(朋友圈)】的数据库操作Service
* @createDate 2026-02-08 18:51:57
*/
@Mapper
public interface MomentService extends IService<Moment> {

    public CreateMomentResponse createMoment(CreateMomentRequest request);

}
