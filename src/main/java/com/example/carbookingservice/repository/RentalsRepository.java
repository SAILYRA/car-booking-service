package com.example.carbookingservice.repository;

import com.example.carbookingservice.entity.Rentals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalsRepository extends JpaRepository<Rentals, Integer> {
    List<Rentals> findByCustomerId(String userId);
}
