package com.example.websocket.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.websocket.model.InMessageInfo;
import com.example.websocket.model.Packet;
import com.example.websocket.utilis.ChannelUtils;
import com.rabbitmq.client.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class MessageListener {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(name=RabbitMqConfig.FANOUT_TRANSFER_MESSAGE_EXCHANGE,type = ExchangeTypes.FANOUT)
    ))
    public void subMessages(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        log.info("[info] {}", msg);
        try {
            Packet<InMessageInfo> inMessageInfoPacket = JSON.parseObject(msg, new TypeReference<Packet<InMessageInfo>>() {
            });
            String userId = inMessageInfoPacket.getData().getUserId();
            List<io.netty.channel.Channel> channels = ChannelUtils.getChannelByUserId(userId);
            // int sum = 1/0;
            for (io.netty.channel.Channel wsChannel : channels) {
                String responseStr = new Date().toString() + wsChannel.id() + " ===>>> " + msg;
                TextWebSocketFrame tws = new TextWebSocketFrame(responseStr);
                wsChannel.writeAndFlush(tws);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 重新入队
            // channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    // @RabbitListener(queues = "cmp.in.msg")
    // public void handleInMessage(Message message, Channel channel) throws Exception{
    //     String msg = new String(message.getBody());
    //     log.info("Rabbitmq accept message, queue: cmp.in.msg, value:{}", msg);
    //     val messageInfo = JSON.parseObject(msg, MessageInfo.class);
    //     val userId = messageInfo.getUserId();
    //     val channels = ChannelUtils.getChannelByUserId(userId);
    //     try{
    //         for (val wsChannel : channels) {
    //             log.info("in message send client channel id:{}, user: {}", wsChannel.id().asShortText(), userId);
    //             wsChannel.writeAndFlush(new TextWebSocketFrame(msg));
    //         }
    //         channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    //     }catch (Exception e){
    //         channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
    //     }
    // }

}