package com.example.carbookingservice.repository;

import com.example.carbookingservice.entity.Vehicles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicles, Integer> {
    Optional<Vehicles> findByVehicleId(String vehicleId);
}
