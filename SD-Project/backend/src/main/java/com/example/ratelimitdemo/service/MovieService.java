package com.example.ratelimitdemo.service;

import com.example.ratelimitdemo.model.Movie;
import com.example.ratelimitdemo.model.MovieSummary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MovieService {

    private final Map<Long, Movie> movieCatalog = Map.of(
        1L, new Movie(1L, "Inception", "Sci-Fi", "English", 148, 9.99),
        2L, new Movie(2L, "The Dark Knight", "Action", "English", 152, 10.99),
        3L, new Movie(3L, "Interstellar", "Sci-Fi", "English", 169, 11.99),
        4L, new Movie(4L, "Parasite", "Thriller", "Korean", 132, 8.99)
    );
    private final ConcurrentHashMap<Long, Integer> seatAvailability = new ConcurrentHashMap<>();

    public MovieService() {
        seatAvailability.put(1L, 30);
        seatAvailability.put(2L, 25);
        seatAvailability.put(3L, 40);
        seatAvailability.put(4L, 20);
    }

    public List<MovieSummary> getAllMovies() {
        List<MovieSummary> summaries = new ArrayList<>();
        for (Movie movie : movieCatalog.values()) {
            summaries.add(toSummary(movie));
        }
        return summaries;
    }

    public MovieSummary getMovieById(Long id) {
        Movie movie = movieCatalog.get(id);
        if (movie == null) {
            return null;
        }
        return toSummary(movie);
    }

    public Movie getMovieEntity(Long id) {
        return movieCatalog.get(id);
    }

    public synchronized int reserveSeats(Long movieId, int seatsToBook) {
        Integer remaining = seatAvailability.get(movieId);
        if (remaining == null || seatsToBook <= 0 || seatsToBook > remaining) {
            return -1;
        }

        int updated = remaining - seatsToBook;
        seatAvailability.put(movieId, updated);
        return updated;
    }

    private MovieSummary toSummary(Movie movie) {
        return new MovieSummary(
            movie.id(),
            movie.title(),
            movie.genre(),
            movie.language(),
            movie.durationMinutes(),
            movie.ticketPrice(),
            seatAvailability.getOrDefault(movie.id(), 0)
        );
    }
}
