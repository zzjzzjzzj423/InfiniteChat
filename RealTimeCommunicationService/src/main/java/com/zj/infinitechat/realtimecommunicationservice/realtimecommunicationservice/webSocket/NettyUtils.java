package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.webSocket;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class NettyUtils {
    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");
    public static AttributeKey<String> UID = AttributeKey.valueOf("userUuid");
    public static AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");
    // 每个Netty都有自己独立的AttribteMap，所以得传入channel
    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey, T data) {
        // 将传入channel的attr容器赋值后存入数据
        Attribute<T> attr = channel.attr(attributeKey);
        attr.set(data);
    }


    public static <T> T getAttr(Channel channel, AttributeKey<T> key) {
        return channel.attr(key).get();
    }

}
