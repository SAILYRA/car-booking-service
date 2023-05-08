package com.example.carbookingservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic bookingCurrencyRequestsTopic(){
        return TopicBuilder.name("bookingCurrencyRequests")
                .build();
    }

    @Bean
    public NewTopic bookingCurrencyResponsesTopic(){
        return TopicBuilder.name("bookingCurrencyResponses")
                .build();
    }

    @Bean
    public NewTopic bookingCarRequestAvailableTopic(){
        return TopicBuilder.name("bookingCarRequestAvailable")
                .build();
    }

    @Bean
    public NewTopic bookingCarRequestNotAvailableTopic(){
        return TopicBuilder.name("bookingCarRequestNotAvailable")
                .build();
    }

    @Bean
    public NewTopic bookingCarResponsesTopic(){
        return TopicBuilder.name("bookingCarCreate")
                .build();
    }
}
