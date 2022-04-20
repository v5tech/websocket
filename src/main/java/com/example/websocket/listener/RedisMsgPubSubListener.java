package com.example.websocket.listener;

import com.example.websocket.utilis.ChannelUtils;
import com.example.websocket.utilis.JedisUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisPubSub;

import java.util.Date;
import java.util.List;

public class RedisMsgPubSubListener extends JedisPubSub {

    private static Logger log = LoggerFactory.getLogger(RedisMsgPubSubListener.class);

    @Override
    public void onMessage(String channelKey, String message) {
        log.info("channel:" + channelKey + ",message:" + message);
        if (StringUtil.isNullOrEmpty(message)) {
            return;
        }
        // 从message中获取用户id
        String userid = message.split("_")[0];
        log.info("userid:" + userid);
        // 从redis中获取该用户的消息
        List<String> msgs;
        try {
            msgs = JedisUtil.getList(userid, 0, -1);
        } catch (Exception e) {
            return;
        }
        if (CollectionUtils.isEmpty(msgs)) {
            return;
        }
        // 获取改用户的所有channel
        List<Channel> channels = ChannelUtils.getChannelByUserId(userid);
        // 遍历每个channel，发送消息
        for (Channel channel : channels) {
            // 遍历每个消息，发送消息
            for (String msg : msgs) {
                String responseStr = new Date().toString() + channel.id() + " ===>>> " + msg;
                TextWebSocketFrame tws = new TextWebSocketFrame(responseStr);
                channel.writeAndFlush(tws);
            }
        }
        // 若是推送完成，清理redis中的消息
        JedisUtil.del(userid);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        log.info("channel:" + channel + "is been subscribed:" + subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        log.info("channel:" + channel + "is been unsubscribed:" + subscribedChannels);
    }

}