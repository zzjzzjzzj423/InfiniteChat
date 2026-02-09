package com.zj.infinitechat.friendmomentservice.controller;


import com.zj.infinitechat.friendmomentservice.common.Result;
import com.zj.infinitechat.friendmomentservice.data.createLike.CreateLikeRequest;
import com.zj.infinitechat.friendmomentservice.data.createLike.CreateLikeResponse;
import com.zj.infinitechat.friendmomentservice.data.createMoment.CreateMomentRequest;
import com.zj.infinitechat.friendmomentservice.data.createMoment.CreateMomentResponse;
import com.zj.infinitechat.friendmomentservice.service.MomentLikeService;
import com.zj.infinitechat.friendmomentservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/moment")
@RequiredArgsConstructor
public class MomentController {
    @Autowired
    private MomentService momentService;

    @Autowired
    private MomentLikeService momentLikeService;

    @PostMapping("")
    public Result<CreateMomentResponse> createMoment(@Valid @RequestBody CreateMomentRequest request) throws Exception {
        CreateMomentResponse response = momentService.createMoment(request);

        return Result.OK(response);
    }

    @PostMapping("/like/{momentId}")
    public Result<CreateLikeResponse> likeMoment(@PathVariable Long momentId, @Valid @RequestBody CreateLikeRequest request) throws Exception {
        CreateLikeResponse response = momentLikeService.likeMomentResponse(momentId, request);

        return Result.OK(response);
    }


}