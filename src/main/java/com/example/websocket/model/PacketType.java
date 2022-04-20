package com.example.websocket.model;

import lombok.Getter;

@Getter
public enum PacketType {
    /**
     * 认证
     */
    auth,
    /**
     * 监控数据
     */
    monitor,
    /**
     * 心跳
     */
    heartbeat,
    /**
     * 任务
     */
    task,
    /**
     * 站内信
     */
    in_message,
    /**
     * 物理设备
     */
    physical_device
}
