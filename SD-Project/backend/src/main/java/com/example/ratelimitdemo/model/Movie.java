package com.example.ratelimitdemo.model;

public record Movie(
    Long id,
    String title,
    String genre,
    String language,
    int durationMinutes,
    double ticketPrice
) {
}
