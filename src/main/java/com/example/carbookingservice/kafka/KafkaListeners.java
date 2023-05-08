package com.example.carbookingservice.kafka;

import com.example.carbookingservice.entity.Rentals;
import com.example.carbookingservice.entity.Vehicles;
import com.example.carbookingservice.repository.VehicleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaListeners {

    private VehicleRepository vehicleRepository;

    @Autowired
    public KafkaListeners(VehicleRepository vehicleRepository){
        this.vehicleRepository = vehicleRepository;
    }

    @KafkaListener(topics = "bookingCarCreate", groupId = "test")
    void createVehicleListener(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> carResponseDataJson;
        try {
            carResponseDataJson = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Konvertieren des JSON-Strings in eine Map<String, Object>.");
        }
        System.out.println(data);
        System.out.println(carResponseDataJson);
        System.out.println(carResponseDataJson.get("carID").toString());
        System.out.println(carResponseDataJson.get("name").toString());
        System.out.println(carResponseDataJson.get("path").toString());

        Vehicles vehicles = new Vehicles();
        Integer.getInteger((String) carResponseDataJson.get("carID"));
        vehicles.setName(carResponseDataJson.get("name").toString());
        vehicles.setPath(carResponseDataJson.get("path").toString());
        vehicles.setAvailable((Boolean) carResponseDataJson.get("isAvailable"));

        vehicleRepository.save(vehicles);
    }
}
