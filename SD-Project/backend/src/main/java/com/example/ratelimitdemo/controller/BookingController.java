package com.example.ratelimitdemo.controller;

import com.example.ratelimitdemo.model.BookingRequest;
import com.example.ratelimitdemo.model.BookingResponse;
import com.example.ratelimitdemo.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/book")
    public ResponseEntity<BookingResponse> bookTicket(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.bookTicket(request));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getBookings() {
        return ResponseEntity.ok(bookingService.getBookingHistory());
    }
}
