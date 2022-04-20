package com.example.websocket.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) {
        log.info("New channel {} from {}.", ctx.channel().id().asShortText(), msg.headers().get("X-Real-IP"));
        log.info("请求url：" + msg.uri() + "|" + msg.method() + "|" + msg.decoderResult() + "|" + msg.protocolVersion());
        ctx.fireChannelRead(msg);
    }

}
