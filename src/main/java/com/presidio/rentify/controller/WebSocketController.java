package com.presidio.rentify.controller;

import com.presidio.rentify.dto.PropertyDTO.PropertyResponseDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/like")
    @SendTo("/topic/likes")
    public PropertyResponseDTO sendLikeUpdate(PropertyResponseDTO property) {
        return property;
    }
}

