package com.example.ratelimitdemo.model;

public record MovieSummary(
    Long id,
    String title,
    String genre,
    String language,
    int durationMinutes,
    double ticketPrice,
    int availableSeats
) {
}
