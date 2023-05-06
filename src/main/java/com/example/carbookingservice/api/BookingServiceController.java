package com.example.carbookingservice.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("car-rental/api/v1")
public class BookingServiceController {
    /*private final InvoicesRepository invoicesRepository;
    private final RentalsRepository rentalsRepository;
    private final VehiclesRepository vehiclesRepository;

    public BookingServiceController(InvoicesRepository invoicesRepository,
                                    RentalsRepository rentalsRepository,
                                    VehiclesRepository vehiclesRepository) {
        this.invoicesRepository = invoicesRepository;
        this.rentalsRepository = rentalsRepository;
        this.vehiclesRepository = vehiclesRepository;
    }

    @PostMapping("/bookings")
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody BookingRequest bookingRequest) {
        double price = 0;

        // Convert currency if needed
        if (!"USD".equals(bookingRequest.getCurrency())) {
            // Use CurrencyConverter API to convert the currency here
            // Add the required library and make an API call to convert the currency
            // Set the converted price to the 'price' variable
        }

        // Create Invoice
        Invoices invoice = new Invoices();
        invoice.setCustomerId(bookingRequest.getUserId());
        invoice.setTotalAmount(bookingRequest.getAmount());
        invoice.setOriginalCurrency("US-Dollar");
        invoice.setTotalAmountSelectedCurrency(price);
        invoice.setSelectedCurrency(bookingRequest.getCurrency());
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setPaymentStatus("Paid");
        invoicesRepository.save(invoice);

        // Get the created invoice ID
        Long invoiceId = invoice.getInvoiceId();

        // Get the vehicle and change the available status to false
        Vehicles vehicle = vehiclesRepository.findById(bookingRequest.getVehicleId())
                .orElseThrow(() -> new IllegalStateException("Vehicle not found"));
        vehicle.setAvailable(false);
        vehiclesRepository.save(vehicle);

        // Create Rental
        Rentals rental = new Rentals();
        rental.setCustomerId(bookingRequest.getUserId());
        rental.setVehicleId(bookingRequest.getVehicleId());
        rental.setStartDate(bookingRequest.getStartDate());
        rental.setEndDate(bookingRequest.getEndDate());
        rental.setTotalDays((int) Duration.between(LocalDateTime.now(), bookingRequest.getEndDate()).toDays());
        rental.setInvoiceId(invoiceId);
        rentalsRepository.save(rental);

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
        */
}
