package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.webSocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class WebSocketTokenAuthHeader extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        log.info("开始校验用户连接资格");
        if (msg instanceof FullHttpRequest){
            log.info("填充用户的useruUid和token");
            FullHttpRequest request = (FullHttpRequest) msg;
            String userUuid = Optional.ofNullable(request.headers()
                    .get("userUuid"))
                    .map(CharSequence::toString)
                    .orElse("");
            String token = Optional.ofNullable(request.headers().get("token"))
                    .map(CharSequence::toString)
                    .orElse("");
            log.info("填充用户的useruUid：{}" , userUuid);
            log.info("填充用户的token: {}" , token);
            NettyUtils.setAttr(ctx.channel(), NettyUtils.UID, userUuid);
            NettyUtils.setAttr(ctx.channel(), NettyUtils.TOKEN, token);


            request.setUri("/zj/netty");
            // ==========================================

            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);


        }else{
            ctx.fireChannelRead(msg);
        }

    }


}
