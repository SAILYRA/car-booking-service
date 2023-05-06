package com.example.carbookingservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    @KafkaListener(topics = "bookingService", groupId = "bookingGroup")
    void listener(String data){
        System.out.println("Listener received: " + data);
    }
}
