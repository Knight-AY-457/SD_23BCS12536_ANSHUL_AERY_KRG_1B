package com.example.ratelimitdemo.model;


public record BookingRequest(Long movieId, String userName, Integer seats) {
}
