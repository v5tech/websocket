package com.example.websocket.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.example.websocket.listener.RedisMsgPubSubListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    // 订阅者列表
    public static final String IM_QUEUE_CHANNLID = "im-queue-channlid";

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new FastJsonRedisSerializer<>(Object.class));
        return template;
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, new PatternTopic(IM_QUEUE_CHANNLID));
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisMsgPubSubListener redisMsgPubSubListener) {
        return new MessageListenerAdapter(redisMsgPubSubListener, "onMessage");
    }

}
