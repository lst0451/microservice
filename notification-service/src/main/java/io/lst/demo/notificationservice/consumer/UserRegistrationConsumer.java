package io.lst.demo.notificationservice.consumer;

import io.lst.demo.common.event.UserRegisteredEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationConsumer {

    @KafkaListener(topics = "${spring.kafka.topic.user-registration}", 
                   groupId = "notification-service",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consume(UserRegisteredEvent event) {
        System.out.println("Received user registration event: " + event);

        // Simulate sending notification
        sendNotification(event);
    }

    private void sendNotification(UserRegisteredEvent event) {
        System.out.println("Sending notification to " + event.getEmail() +
                " for user " + event.getUsername());
    }
}
