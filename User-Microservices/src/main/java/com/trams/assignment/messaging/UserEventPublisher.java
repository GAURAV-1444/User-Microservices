package com.trams.assignment.messaging;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trams.assignment.event.UserCreatedEvent;

import io.nats.client.Connection;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final Connection natsConnection;
    private final ObjectMapper objectMapper;

    public void publishUserCreated(UserCreatedEvent event) {

        try {
            String message =
                    objectMapper.writeValueAsString(event);

            natsConnection.publish(
                    "user.created",
                    message.getBytes()
            );

            natsConnection.flush(Duration.ofSeconds(1));

            System.out.println(
                    "USER_CREATED event published: " + message
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to publish USER_CREATED event",
                    e
            );
        }
    }
}