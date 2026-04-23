package com.example.ratelimitdemo.interceptor;

import com.example.ratelimitdemo.ratelimiter.ApiKeyPlan;
import com.example.ratelimitdemo.ratelimiter.RateLimiterService;
import io.github.bucket4j.ConsumptionProbe;
import com.example.ratelimitdemo.config.InvalidApiKeyException;
import com.example.ratelimitdemo.config.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RateLimitInterceptor.class);
    private static final String API_KEY_HEADER = "X-API-KEY";

    private final RateLimiterService rateLimiterService;

    public RateLimitInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow CORS preflight requests without API key checks.
        if (RequestMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        String endpoint = request.getRequestURI();

        if (apiKey == null || ApiKeyPlan.getLimitForKey(apiKey) == null) {
            log.info("apiKey={} endpoint={} result=BLOCKED_INVALID_KEY", apiKey, endpoint);
            throw new InvalidApiKeyException("Missing or invalid API key");
        }

        ConsumptionProbe probe = rateLimiterService.consumeToken(apiKey);
        if (probe.isConsumed()) {
            log.info("apiKey={} endpoint={} result=ALLOWED", apiKey, endpoint);
            return true;
        }

        long retryAfterSeconds = (long) Math.ceil(probe.getNanosToWaitForRefill() / 1_000_000_000.0);
        if (retryAfterSeconds <= 0) {
            retryAfterSeconds = 1;
        }

        log.info("apiKey={} endpoint={} result=BLOCKED_RATE_LIMIT", apiKey, endpoint);
        throw new RateLimitExceededException("Rate limit exceeded. Try again later.", retryAfterSeconds);
    }
}
