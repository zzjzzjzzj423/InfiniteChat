package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.webSocket;


import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Slf4j
public class NettyServer {

    @Value("${netty.port}")
    private int port;

    @Value("${netty.name}")
    private String serverName;

    @Autowired
    private NacosServiceManager nacosServiceManager;

    @Autowired
    private StringRedisTemplate redisTemplate;


    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private EventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors());
    // 使用事件监听代替 @PostConstruct
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        // 开启新线程异步启动，防止阻塞 Spring 主线程
        new Thread(() -> {
            try {
                // 1. 启动 Netty
                run();

                // 2. 注册到 Nacos (此时容器已就绪，不会再报 NPE)
                NamingService namingService = nacosServiceManager.getNamingService();
                String ip = InetAddress.getLocalHost().getHostAddress();
                namingService.registerInstance(serverName, ip, port);

                log.info("✅ Netty Server 启动并注册成功 | 端口: {} | IP: {}", port, ip);
            } catch (Exception e) {
                log.error("❌ Netty Server 启动或注册失败", e);
                destroy(); // 失败则清理资源
            }
        }).start();
    }

    private void run() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors());

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(300, 0, 0));
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        // 你的自定义 Handler
                        pipeline.addLast(new WebSocketTokenAuthHeader());
                        pipeline.addLast(new WebSocketServerProtocolHandler("/zj/netty"));
                        pipeline.addLast(new MessageInboundHandler(redisTemplate));
                    }
                });

        // 绑定端口并同步等待成功
        serverBootstrap.bind(port).sync();
    }

    @PreDestroy
    public void destroy() {
        log.info("Shutting down Netty Server...");
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();
    }


}
