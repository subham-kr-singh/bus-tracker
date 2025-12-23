package com.bus_tracker.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusLocationHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/location")
    public void handleLocation(String message) {
        messagingTemplate.convertAndSend("/topic/bus/1", message);
    }
}
