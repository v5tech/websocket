package com.example.websocket.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.websocket.config.MessageSender;
import com.example.websocket.model.InMessageInfo;
import com.example.websocket.model.Packet;
import com.example.websocket.model.PacketType;
import com.example.websocket.model.WsResponse;
import com.example.websocket.utilis.ChannelUtils;
import com.example.websocket.utilis.Utility;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;

@Slf4j
public class InMessageHandler extends SimpleChannelInboundHandler<Packet<InMessageInfo>> {

    public static final String PING = "ping";

    private MessageSender messageSender;

    public InMessageHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet<InMessageInfo> msg) {
        val packet = JSON.parseObject(JSON.toJSONString(msg), new TypeReference<Packet<InMessageInfo>>() {
        });
        log.info("InMessageHandler message : {}", JSON.toJSONString(packet.getData(), true));
        if (packet.getData() == null) {
            Utility.sendResponse(ctx.channel(), new WsResponse(WsResponse.ERROR, "InMessage data is empty."));
        }
        val userId = packet.getData().getUserId();
        String channelId = ctx.channel().id().asShortText();
        val key = userId + "_" + channelId;
        log.info("in message create channel id is {},user id is {}.", channelId, userId);
        if (packet.getData().getType() != null && PING.equals(packet.getData().getType())) {
            ChannelUtils.put(key, ctx.channel());
            log.info("保存连接的channel：{}", channelId);
            messageSender.convertAndSend(JSON.toJSONString(packet));
        } else {
            ChannelUtils.put(key, ctx.channel());
            log.info("保存连接的channel：{}", channelId);
            // TODO: 查询业务逻辑
            // 调用 cmp获取相关的数据
            // val message = cmpClient.getMessageList(packet.getData().getUserId());
            messageSender.convertAndSend(JSON.toJSONString(packet));
        }
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return super.acceptInboundMessage(msg) && ((Packet) msg).getType() == PacketType.in_message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Error on InMessageHandler", cause);
        Utility.sendResponse(ctx.channel(), new WsResponse(WsResponse.ERROR, "InMessage error."));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("新的Channel 连接");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        List<String> keyByChannel = ChannelUtils.getChannelByChannel(ctx.channel());
        ChannelUtils.remove(ctx.channel());
        log.info("连接断开，删除相关Channel ：{}", JSON.toJSONString(keyByChannel));
    }
}
