package com.example.ratelimitdemo.ratelimiter;

import java.util.Map;


public final class ApiKeyPlan {

    public static final String FREE_USER_KEY = "FREE_USER_KEY";
    public static final String PREMIUM_USER_KEY = "PREMIUM_USER_KEY";

    private static final Map<String, Long> LIMITS_PER_MINUTE = Map.of(
        FREE_USER_KEY, 3L,
        PREMIUM_USER_KEY, 7L
    );

    private ApiKeyPlan() {
    }

    public static Long getLimitForKey(String apiKey) {
        return LIMITS_PER_MINUTE.get(apiKey);
    }
}
