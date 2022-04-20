package com.example.websocket.utilis;

import io.netty.channel.Channel;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ChannelUtils {

    private static final Map<String, Channel> MESSAGE_CHANNEL_MAP = new ConcurrentHashMap<>();

    public static void put(String key, Channel channel) {
        if (MESSAGE_CHANNEL_MAP.get(key) != null) {
            return;
        }
        MESSAGE_CHANNEL_MAP.put(key, channel);
    }

    public static List<Channel> getChannelByUserId(String userId) {
        return MESSAGE_CHANNEL_MAP.entrySet().stream().filter(entry -> entry.getKey().startsWith(userId)).map(x -> x.getValue()).collect(Collectors.toList());
    }

    public static Channel get(String key) {
        return MESSAGE_CHANNEL_MAP.get(key);
    }

    public static void remove(Channel channel) {
        val list = new ArrayList<String>();
        MESSAGE_CHANNEL_MAP.entrySet().stream().filter(entry -> entry.getValue().id().equals(channel.id()))
                .forEach(entry -> list.add(entry.getKey()));
        for (String key : list) {
            MESSAGE_CHANNEL_MAP.remove(key);
        }
    }

    public static List<String> getChannelByChannel(Channel channel) {
        val list = new ArrayList<String>();
        MESSAGE_CHANNEL_MAP.entrySet().stream().filter(entry -> entry.getValue() == channel).forEach(entry -> list.add(entry.getKey()));
        return list;
    }
}