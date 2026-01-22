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
        if (msg instanceof FullHttpRequest){

            FullHttpRequest request = (FullHttpRequest) msg;
            String userUuid = Optional.ofNullable(request.headers()
                    .get("userUuid"))
                    .map(CharSequence::toString)
                    .orElse("");
            String token = Optional.ofNullable(request.headers().get("token"))
                    .map(CharSequence::toString)
                    .orElse("");

            NettyUtils.setAttr(ctx.channel(), NettyUtils.UID, userUuid);
            NettyUtils.setAttr(ctx.channel(), NettyUtils.TOKEN, token);


            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);

        }else{
            ctx.fireChannelRead(msg);
        }

    }


}
