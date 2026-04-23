package com.example.ratelimitdemo.controller;

import com.example.ratelimitdemo.model.MovieSummary;
import com.example.ratelimitdemo.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }
    @GetMapping
    public List<MovieSummary> getMovies() {
        return movieService.getAllMovies();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        MovieSummary movie = movieService.getMovieById(id);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Movie not found"));
        }
        return ResponseEntity.ok(movie);
    }
}
