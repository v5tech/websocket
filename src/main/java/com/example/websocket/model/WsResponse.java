package com.example.websocket.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WsResponse {
    private int code;
    private Object body;

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;

    public WsResponse() {
    }

    public WsResponse(int code, String body) {
        this.code = code;
        this.body = body;
    }
}
