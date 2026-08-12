package com.trams.assignment.messaging;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trams.assignment.event.UserCreatedEvent;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEventSubscriber {

    private static final Logger logger =
            LoggerFactory.getLogger(UserEventSubscriber.class);

    private final Connection natsConnection;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void subscribe() {

        try {

            Dispatcher dispatcher =
                    natsConnection.createDispatcher(
                            new io.nats.client.MessageHandler() {

                                @Override
                                public void onMessage(
                                        Message message) {

                                    try {

                                        String json =
                                                new String(
                                                        message.getData(),
                                                        StandardCharsets.UTF_8
                                                );

                                        UserCreatedEvent event =
                                                objectMapper.readValue(
                                                        json,
                                                        UserCreatedEvent.class
                                                );

                                        logger.info(
                                                "USER_CREATED event received: {}",
                                                json
                                        );

                                        logger.info(
                                                "Notification: Welcome {}!",
                                                event.getName()
                                        );

                                    } catch (Exception e) {

                                        logger.error(
                                                "Failed to process USER_CREATED event",
                                                e
                                        );
                                    }
                                }
                            }
                    );

            dispatcher.subscribe("user.created");

            logger.info(
                    "Subscribed to NATS subject: user.created"
            );

        } catch (Exception e) {

            logger.error(
                    "Failed to subscribe to NATS",
                    e
            );

            throw new RuntimeException(
                    "Failed to subscribe to NATS",
                    e
            );
        }
    }
}