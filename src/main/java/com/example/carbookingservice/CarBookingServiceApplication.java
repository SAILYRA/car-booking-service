package com.example.carbookingservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class CarBookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarBookingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(KafkaTemplate<String, String> kafkaTemplate){
        return  args -> {
          kafkaTemplate.send("bookingService", "hello Test");
        };
    }
}
