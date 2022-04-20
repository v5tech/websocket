package com.example.websocket.utilis;

import com.alibaba.fastjson.JSON;
import com.example.websocket.model.WsResponse;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.val;

public class Utility {

    public static void sendResponse(Channel channel, WsResponse wsResponse) {
        val response = new TextWebSocketFrame(JSON.toJSONString(wsResponse));
        channel.writeAndFlush(response);
    }

}
