package com.example.ratelimitdemo.service;

import com.example.ratelimitdemo.model.BookingRequest;
import com.example.ratelimitdemo.model.BookingResponse;
import com.example.ratelimitdemo.model.Movie;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookingService {

    private final MovieService movieService;
    private final AtomicLong bookingCounter = new AtomicLong(1000);
    private final List<BookingResponse> bookingHistory = new CopyOnWriteArrayList<>();

    public BookingService(MovieService movieService) {
        this.movieService = movieService;
    }

    public BookingResponse bookTicket(BookingRequest request) {
        validateRequest(request);

        Movie movie = movieService.getMovieEntity(request.movieId());
        if (movie == null) {
            throw new NoSuchElementException("Movie not found");
        }

        int remainingSeats = movieService.reserveSeats(request.movieId(), request.seats());
        if (remainingSeats < 0) {
            throw new IllegalArgumentException("Not enough seats available for this movie");
        }

        BookingResponse response = new BookingResponse(
            bookingCounter.incrementAndGet(),
            "Booking successful",
            movie.id(),
            movie.title(),
            request.userName().trim(),
            request.seats(),
            request.seats() * movie.ticketPrice(),
            remainingSeats,
            LocalDateTime.now().toString()
        );
        bookingHistory.add(response);
        return response;
    }

    public List<BookingResponse> getBookingHistory() {
        return bookingHistory;
    }

    private void validateRequest(BookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.movieId() == null) {
            throw new IllegalArgumentException("movieId is required");
        }
        if (request.userName() == null || request.userName().trim().isEmpty()) {
            throw new IllegalArgumentException("userName is required");
        }
        if (request.seats() == null || request.seats() <= 0) {
            throw new IllegalArgumentException("seats must be greater than 0");
        }
    }
}
