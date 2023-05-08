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

    @KafkaListener(topics = "bookingCarRequestNotAvailable", groupId = "carGroup")
    void setVehicleToNotAvailable(String data) {
        System.out.println("Car Service: " + data);
    }

    @KafkaListener(topics = "bookingCarRequestAvailable", groupId = "carGroup")
    void setVehicleToAvailable(String data) {
        System.out.println("Car Service: " + data);
    }

    @KafkaListener(topics = "bookingCarResponses", groupId = "carGroup")
    void createVehicleListener(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> carResponseDataJson;
        try {
            carResponseDataJson = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Konvertieren des JSON-Strings in eine Map<String, Object>.");
        }

        Vehicles vehicles = new Vehicles();
        vehicles.setVehicleId(carResponseDataJson.get("carID").toString());
        vehicles.setName(carResponseDataJson.get("name").toString());
        vehicles.setPath(carResponseDataJson.get("path").toString());
        vehicles.setAvailable((Boolean) carResponseDataJson.get("isAvaiable"));

        vehicleRepository.save(vehicles);
    }
}
