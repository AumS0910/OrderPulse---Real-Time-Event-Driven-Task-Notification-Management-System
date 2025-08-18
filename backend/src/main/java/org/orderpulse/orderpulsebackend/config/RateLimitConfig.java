package org.orderpulse.orderpulsebackend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration class for rate limiting using the token bucket algorithm.
 * Uses Bucket4j library to implement rate limiting functionality.
 */
@Configuration
public class RateLimitConfig {

    /**
     * Creates a new rate limit bucket with the following constraints:
     * - 100 requests per minute
     * - Greedy refill strategy (tokens are added as soon as possible)
     * @return A new Bucket instance configured with the rate limit
     */
    public Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}