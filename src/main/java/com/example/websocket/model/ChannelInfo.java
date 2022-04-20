package com.example.websocket.model;

import lombok.Data;

import java.util.ArrayDeque;
import java.util.Queue;

@Data
public class ChannelInfo {
    private String id;
    private String nodeId;
    private long accountId;
    private Queue<String> content = new ArrayDeque<>();
}
