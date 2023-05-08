package com.example.carbookingservice.repository;

import com.example.carbookingservice.entity.Invoices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoicesRepository  extends JpaRepository<Invoices, Integer> {
}
