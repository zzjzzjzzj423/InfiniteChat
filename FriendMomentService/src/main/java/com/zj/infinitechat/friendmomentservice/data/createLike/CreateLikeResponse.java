package com.zj.infinitechat.friendmomentservice.data.createLike;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateLikeResponse {
    /**
     * 点赞ID
     */
    private Long likeId;
}