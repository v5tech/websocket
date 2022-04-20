package com.example.websocket.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.websocket.model.MsgInfo;
import com.example.websocket.utilis.ChannelUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RedisMsgPubSubListener {

    private static Logger log = LoggerFactory.getLogger(RedisMsgPubSubListener.class);

    public void onMessage(Object message) {
        log.info("接收到PUSH消息：{}", message);
        MsgInfo msgInfo = JSON.parseObject(message.toString(), new TypeReference<MsgInfo>() {
        });
        String userid = msgInfo.getUserId();
        log.info("userid:" + userid);
        // 获取该用户的所有channel
        List<Channel> channels = ChannelUtils.getChannelByUserId(userid);
        // 遍历每个channel，发送消息
        for (Channel channel : channels) {
            String responseStr = new Date().toString() + channel.id() + " ===>>> " + msgInfo.getMsg();
            TextWebSocketFrame tws = new TextWebSocketFrame(responseStr);
            channel.writeAndFlush(tws);
        }
    }

}