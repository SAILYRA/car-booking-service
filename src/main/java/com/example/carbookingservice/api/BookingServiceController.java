package com.example.carbookingservice.api;

import com.example.carbookingservice.entity.Invoices;
import com.example.carbookingservice.entity.Rentals;
import com.example.carbookingservice.entity.Vehicles;
import com.example.carbookingservice.repository.InvoicesRepository;
import com.example.carbookingservice.repository.RentalsRepository;
import com.example.carbookingservice.repository.VehicleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("car-rental/api/v1")
public class BookingServiceController {
    private KafkaTemplate<String, String> kafkaTemplate;
    private ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;
    private final RentalsRepository rentalsRepository;
    private final InvoicesRepository invoicesRepository;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public BookingServiceController(
            KafkaTemplate<String, String> kafkaTemplate,
            ReplyingKafkaTemplate replyingKafkaTemplate,
            RentalsRepository rentalsRepository,
            InvoicesRepository invoicesRepository,
            VehicleRepository vehicleRepository
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.replyingKafkaTemplate = replyingKafkaTemplate;
        this.rentalsRepository = rentalsRepository;
        this.invoicesRepository = invoicesRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @PostMapping("/bookings")
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody BookingRequest bookingRequest) throws ExecutionException, InterruptedException, TimeoutException {
        double price = 0;
        double basePrice = 0;
        // Get the vehicle and change the available status to false
        try {
            kafkaTemplate.send("bookingCarRequestNotAvailable", bookingRequest.getVehicleId());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Convert currency if needed
        if (!"USD".equals(bookingRequest.getCurrency())) {
            Map<String, Object> currencyData = new HashMap<>();
            currencyData.put("baseCurrency", bookingRequest.getCurrency());
            currencyData.put("targetCurrency", "USD");
            currencyData.put("amount", bookingRequest.getAmount());

            ObjectMapper objectMapper = new ObjectMapper();
            String currencyDataJsonString;
            try {
                currencyDataJsonString = objectMapper.writeValueAsString(currencyData);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException("Convertion error");
            }
            try {
                if (!replyingKafkaTemplate.waitForAssignment(Duration.ofSeconds(10))) {
                    throw new IllegalStateException("Reply container did not initialize");
                }
                ProducerRecord<String, String> record = new ProducerRecord<>("bookingCurrencyRequests", currencyDataJsonString);
                RequestReplyFuture<String, String, String> replyFuture = replyingKafkaTemplate.sendAndReceive(record);
                SendResult<String, String> sendResult = replyFuture.getSendFuture().get(30, TimeUnit.SECONDS);
                System.out.println("Sent ok: " + sendResult.getRecordMetadata());
                ConsumerRecord<String, String> consumerRecord = replyFuture.get(30, TimeUnit.SECONDS);
                basePrice = Double.parseDouble(consumerRecord.value());
                price = bookingRequest.getAmount();
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            basePrice = bookingRequest.getAmount();
            price = bookingRequest.getAmount();
        }


        // Create Invoice
        Invoices invoice = new Invoices();
        invoice.setCustomerId(bookingRequest.getUserId());
        invoice.setTotalAmount(basePrice);
        invoice.setOriginalCurrency("USD");
        invoice.setTotalAmountSelectedCurrency(price);
        invoice.setSelectedCurrency(bookingRequest.getCurrency());
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setPaymentStatus(Invoices.PaymentStatus.PAID);
        invoicesRepository.save(invoice);

        // Get the created invoice ID
        Integer invoiceId = invoice.getInvoiceId();

        // Create Rental
        Rentals rental = new Rentals();
        rental.setCustomerId(bookingRequest.getUserId());
        rental.setVehicleId(bookingRequest.getVehicleId());
        rental.setStartDate(bookingRequest.getStartDate());
        rental.setEndDate(bookingRequest.getEndDate());

        LocalDateTime now = LocalDateTime.now();
        rental.setCreatedAt(now);
        rental.setUpdatedAt(now);

        LocalDateTime startDateTime = bookingRequest.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = bookingRequest.getEndDate().atStartOfDay();
        rental.setTotalDays((int) Duration.between(startDateTime, endDateTime).toDays());

        rental.setInvoiceId(invoiceId);
        rentalsRepository.save(rental);

        Vehicles vehicle = vehicleRepository.findByVehicleId(bookingRequest.getVehicleId()).orElseThrow();
        vehicle.setAvailable(false);
        vehicleRepository.save(vehicle);

        // Return response if everything is successful
        Map<String, Object> response = new HashMap<>();
        response.put("booking_id", rental.getRentalId());
        response.put("vehicle_id", bookingRequest.getVehicleId());
        response.put("user_id", bookingRequest.getUserId());
        response.put("start_date", bookingRequest.getStartDate());
        response.put("end_date", bookingRequest.getEndDate());
        response.put("created_at", rental.getCreatedAt());
        response.put("updated_at", rental.getUpdatedAt());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    // Get a specific car booking
    @GetMapping("/bookings/{id}")
    public ResponseEntity<Map<String, Object>> getBooking(@PathVariable("id") Integer id) {
        Optional<Rentals> bookingOpt = rentalsRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Rentals booking = bookingOpt.get();

            // Fetch the invoice data using the invoice_id
            Invoices invoice = invoicesRepository.findById(booking.getInvoiceId()).orElseThrow();

            // Fetch the vehicle data using the vehicle_id
            Vehicles vehicles = vehicleRepository.findByVehicleId(booking.getVehicleId()).orElseThrow();

            // Combine the booking, invoice, and vehicle data into a single map
            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("booking", booking);
            bookingData.put("invoice", invoice);
            bookingData.put("vehicle", vehicles);

            return new ResponseEntity<>(bookingData, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all bookings for a specific user
    @GetMapping("/users/{user_id}/bookings")
    public ResponseEntity<List<Map<String, Object>>> getUserBookings(@PathVariable("user_id") String userId) {
        List<Rentals> bookings = rentalsRepository.findByCustomerId(userId);
        List<Map<String, Object>> data = new ArrayList<>();

        for (Rentals booking : bookings) {
            Optional<Vehicles> vehicleOpt = vehicleRepository.findByVehicleId(booking.getVehicleId());
            if (vehicleOpt.isPresent()) {
                Vehicles vehicle = vehicleOpt.get();
                Map<String, Object> entry = new HashMap<>();
                entry.put("booking", booking);
                entry.put("vehicle_type", vehicle);
                data.add(entry);
            }
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    // Delete a car booking
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable("id") Integer id) {
        Optional<Rentals> bookingOpt = rentalsRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Rentals booking = bookingOpt.get();

            // Get the vehicle_id and change the available status to true
            Vehicles vehicle = vehicleRepository.findByVehicleId(booking.getVehicleId()).orElseThrow();
            vehicle.setAvailable(true);
            vehicleRepository.save(vehicle);

            rentalsRepository.delete(booking);

            try {
                kafkaTemplate.send("bookingCarRequestAvailable", vehicle.getVehicleId());
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }


            return new ResponseEntity<>("Deletion success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Cancel a car booking
    @PostMapping("/bookings/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable("id") Integer id) {
        Optional<Rentals> bookingOpt = rentalsRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Rentals booking = bookingOpt.get();

            // Get the vehicle_id and change the available status to true
            Vehicles vehicle = vehicleRepository.findByVehicleId(booking.getVehicleId()).orElseThrow();
            vehicle.setAvailable(true);
            vehicleRepository.save(vehicle);

            try {
                kafkaTemplate.send("bookingCarRequestAvailable", vehicle.getVehicleId());
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }


            return new ResponseEntity<>("Cancellation success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
