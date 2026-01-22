package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.webSocket;


import cn.hutool.json.JSONUtil;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.Utils.JWTUtils;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.constants.MessageTypeEnum;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.constants.UserConstants;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.exception.MessageTypeException;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.model.AckData;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.model.LogOutData;
import com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.model.MessageDTO;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.InetAddress;

@Slf4j
@ChannelHandler.Sharable
@AllArgsConstructor
public class MessageInboundHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        log.info("建立Websocket连接");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        offline(ctx);

        super.channelInactive(ctx);
    }

    /*
    * 这个是websocket的函数， 当服务器收到消息的时候会回调这个函数 channelRead0
    * 值得一提， 两个参数
    * 1. ChannelHandlerContext是客户端与netty建立连接的入口， 它可以返回消息给客户端
    *       1.1  ChannelHandlerContext和Channel有很大区别的，
    *            ChannelHandlerContext是属于在当前的Handler所存储的信息，
    *            而Channel是这条客户端连接本身
    *
    * 2. TextWebSocketFrame是指发送的文本消息 基于Frame协议是对文本消息的封装
    *
    * */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        log.info("后端收到消息, 准备进行处理");


        // 解析文本消息， 并且反序列化到messageDto中
        MessageDTO messageDTO = JSONUtil.toBean(textWebSocketFrame.text(), MessageDTO.class);

        MessageTypeEnum messageType = MessageTypeEnum.of(messageDTO.getType());
        switch (messageType){
            case ACK:
            processACK(messageDTO);
            case LOG_OUT:
            processLogOut(channelHandlerContext, messageDTO);
            case HEART_BEAT:
            processHeartBeat(channelHandlerContext);
            default:


        }
    }

    private void processACK(MessageDTO msg){
        AckData ackData = JSONUtil.toBean(msg.getData().toString(), AckData.class);
        log.info("ackData:{}", ackData);
        log.info("消息成功推送");
    }


    private void processLogOut(ChannelHandlerContext ctx, MessageDTO msg){
        LogOutData logOutData = JSONUtil.toBean(msg.getData().toString(), LogOutData.class);
        Integer userUuid = logOutData.getUserUuid();
        log.info("请求断开用户{}的连接...",userUuid);
        offline(ctx);
        log.info("断开连接成功！");
    }

    private void processHeartBeat(ChannelHandlerContext ctx){
        log.info("收到客户端心跳");
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setType(MessageTypeEnum.HEART_BEAT.getCode());
        TextWebSocketFrame frame = new TextWebSocketFrame(JSONUtil.toJsonStr(messageDTO));
        ctx.channel().writeAndFlush(frame);
    }



    public void offline(ChannelHandlerContext ctx){
        String userUuid = ChannelManager.getUserByChannel(ctx.channel());

        try{
            ChannelManager.removeChannelUser(ctx.channel());
            if (userUuid != null){
                ChannelManager.removeUserChannel(userUuid);
                log.info("客户端关闭连接UserId：{}, 客户端地址为：{}",userUuid, ctx.channel().remoteAddress());
            }

        }catch (Exception e){
            log.error("处理退出登录异常", e);
        }finally {
            if(ctx.channel() != null){
                ctx.channel().close();
            }
            stringRedisTemplate.opsForValue().getAndDelete(UserConstants.USER_SESSION+userUuid);

        }
    }


    //这个是当连接发生事件的时候调用如IdleStateEvent等等
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;

            switch (event.state()){
                case READER_IDLE:
                    log.error("读空闲超时，关闭连接...{}, 用户ID{}",ctx.channel().remoteAddress(), ChannelManager.getUserByChannel(ctx.channel()));
                    offline(ctx);
                    break;
                case WRITER_IDLE:
                    log.error("写空闲超时");
                case ALL_IDLE:
                    log.error("读写空闲超时");
            }

        }

        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            String token = NettyUtils.getAttr(ctx.channel(), NettyUtils.TOKEN);
            String userUuid = NettyUtils.getAttr(ctx.channel(), NettyUtils.UID);
            if(!validate(userUuid, token)){
                log.info("token invalid");
                ctx.close();
                return;
            }
            stringRedisTemplate.opsForValue().set(UserConstants.USER_SESSION+userUuid, InetAddress.getLocalHost().getHostAddress());

            Channel channel = ChannelManager.getChannelByUserId(userUuid);
            if (channel!=null){
                ChannelManager.removeUserChannel(userUuid);
                ChannelManager.removeChannelUser(channel);
                channel.close();
            }

            ChannelManager.addUserChannel(userUuid,ctx.channel());
            ChannelManager.addChannelUser(userUuid, ctx.channel());

            log.info("客户连接成功， 用户ID：{}",userUuid + "管道地址： " + ctx.channel().remoteAddress());

        }

    }

    private boolean validate(String userUuid, String token){
        Claims claims = JWTUtils.parse(token);

        String checkUserid = claims.getSubject();

        return checkUserid.equals(userUuid);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.error("捕获到异常：", cause);

        try {
            offline(ctx);
        }catch (Exception e){
            log.error("关闭管道失败", e);
        }
    }



    private void processIllegal(MessageDTO msg){
        throw new MessageTypeException("不支持的消息格式！");
    }

}
