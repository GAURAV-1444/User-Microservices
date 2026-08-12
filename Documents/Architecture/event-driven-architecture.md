# Event-Driven Architecture

## Overview

The User Microservice and Notification Microservice communicate asynchronously using NATS.

They do not communicate using REST APIs or WebSockets.


## Event Flow

1. User registers successfully.
2. User is persisted in MySQL.
3. User Microservice creates a `UserCreatedEvent`.
4. Event is published to the NATS subject `user.created`.
5. Notification Microservice subscribes to `user.created`.
6. The event is deserialized.
7. Notification processing is performed.

## Event

```json
{
  "userId": 6,
  "name": "Gaurav",
  "email": "gaurav@example.com"
}
```

## NATS Subject

```text
user.created
```

## Producer

User Microservice publishes the event.

## Consumer

Notification Microservice subscribes to the event and processes the notification.

## Communication Principle

The Notification Microservice does not require a REST call from the User Microservice.