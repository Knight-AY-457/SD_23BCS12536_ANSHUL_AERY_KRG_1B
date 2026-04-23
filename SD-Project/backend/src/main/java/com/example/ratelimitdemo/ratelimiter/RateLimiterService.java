package com.example.ratelimitdemo.ratelimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final ConcurrentHashMap<String, Bucket> bucketStore = new ConcurrentHashMap<>();

    public ConsumptionProbe consumeToken(String apiKey) {
        Bucket bucket = bucketStore.computeIfAbsent(apiKey, this::newBucketForKey);
        return bucket.tryConsumeAndReturnRemaining(1);
    }

    private Bucket newBucketForKey(String apiKey) {
        Long limitPerMinute = ApiKeyPlan.getLimitForKey(apiKey);
        if (limitPerMinute == null) {
            limitPerMinute = 0L;
        }

        Bandwidth bandwidth = Bandwidth.classic(
            limitPerMinute,
            Refill.greedy(limitPerMinute, Duration.ofMinutes(1))
        );
        return Bucket4j.builder()
            .addLimit(bandwidth)
            .build();
    }
}
