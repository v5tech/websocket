package com.example.websocket;

import com.example.websocket.config.AppProperties;
import com.example.websocket.config.MessageSender;
import com.example.websocket.ws.HttpRequestHandler;
import com.example.websocket.ws.InMessageHandler;
import com.example.websocket.ws.TextWebSocketFrameHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class Application implements CommandLineRunner {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private MessageSender messageSender;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        val parentGroup = new NioEventLoopGroup();
        val childGroup = new NioEventLoopGroup();
        val bootstrap = new ServerBootstrap();
        bootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new HttpRequestHandler());
                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        ch.pipeline().addLast(new HttpObjectAggregator(8192));
                        ch.pipeline().addLast(new WebSocketServerCompressionHandler());
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler(appProperties.getPath(), null, true, 6553500));
                        ch.pipeline().addLast(new TextWebSocketFrameHandler());
                        ch.pipeline().addLast(new InMessageHandler(messageSender));
                    }
                    @Override
                    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                        log.info("channel unregistered: " + ctx.channel());
                        super.handlerRemoved(ctx);
                    }
                });
        val future = bootstrap.bind(appProperties.getPort()).sync();
        if (future.isSuccess()) {
            log.info("Server start success.");
        } else {
            log.info("Server start failed.");
            future.cause().printStackTrace();
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
        future.channel().closeFuture().sync();
    }

}
