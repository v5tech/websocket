package com.example.websocket.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class Packet<T> {
    private T data;
    private PacketType type;
    private OffsetDateTime time;
    private String endpoint;
}
