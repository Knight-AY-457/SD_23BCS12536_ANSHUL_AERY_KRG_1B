package com.example.ratelimitdemo.model;

public record BookingResponse(
    long bookingId,
    String message,
    Long movieId,
    String movieTitle,
    String userName,
    int seatsBooked,
    double totalAmount,
    int remainingSeats,
    String bookedAt
) {
}
