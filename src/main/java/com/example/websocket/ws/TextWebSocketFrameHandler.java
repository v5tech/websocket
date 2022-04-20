package com.example.websocket.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.example.websocket.model.Packet;
import com.example.websocket.model.PacketType;
import com.example.websocket.model.WsResponse;
import com.example.websocket.utilis.Utility;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.util.ObjectUtils;

@Slf4j
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.debug("Message received: " + msg.text().length());
        if (!msg.isFinalFragment()) {
            return;
        }
        val response = new WsResponse();
        try {
            val packet = JSON.parseObject(msg.text(), Packet.class);
            if (!PacketType.in_message.equals(packet.getType())) {
                if (ObjectUtils.isEmpty(packet.getEndpoint()) || packet.getTime() == null || packet.getType() == null || packet.getData() == null) {
                    Utility.sendResponse(ctx.channel(), response.setCode(WsResponse.ERROR).setBody("Packet is invalid."));
                    return;
                }
                log.debug("Message date info, instance object_id(endpoint):{}, type:{}", packet.getEndpoint(), packet.getType());
            }
            ctx.fireChannelRead(packet);
        } catch (JSONException e) {
            log.error("Parse message to json failed: {}.", msg.text());
            Utility.sendResponse(ctx.channel(), response.setCode(WsResponse.ERROR).setBody("Parse message to json failed."));
        }
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return super.acceptInboundMessage(msg);
    }
}
