package com.example.carbookingservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Integer invoiceId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "original_currency")
    private String originalCurrency;

    @Column(name = "total_amount_selected_currency")
    private double totalAmountSelectedCurrency;

    @Column(name = "selected_currency")
    private String selectedCurrency;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and setters for all properties

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOriginalCurrency() {
        return originalCurrency;
    }

    public void setOriginalCurrency(String originalCurrency) {
        this.originalCurrency = originalCurrency;
    }

    public double getTotalAmountSelectedCurrency() {
        return totalAmountSelectedCurrency;
    }

    public void setTotalAmountSelectedCurrency(double totalAmountSelectedCurrency) {
        this.totalAmountSelectedCurrency = totalAmountSelectedCurrency;
    }

    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    public void setSelectedCurrency(String selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public enum PaymentStatus {
        PAID, PENDING
    }
}
