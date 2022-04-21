package com.example.websocket.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class MessageInfo implements Serializable {

    private Integer count;

    private String userId;

}
