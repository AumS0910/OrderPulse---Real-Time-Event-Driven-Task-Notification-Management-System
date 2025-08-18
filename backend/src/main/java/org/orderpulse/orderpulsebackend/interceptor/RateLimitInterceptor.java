package org.orderpulse.orderpulsebackend.interceptor;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.orderpulse.orderpulsebackend.config.RateLimitConfig;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that implements rate limiting for API endpoints.
 * Uses token bucket algorithm via Bucket4j to control request rates.
 */
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitConfig rateLimitConfig;
    private final Bucket bucket;

    /**
     * Constructor that initializes the rate limiter bucket.
     * @param rateLimitConfig Configuration for creating rate limit buckets
     */
    public RateLimitInterceptor(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
        this.bucket = rateLimitConfig.createNewBucket();
    }

    /**
     * Handles pre-processing of requests to apply rate limiting.
     * Adds rate limit headers and returns appropriate status codes:
     * - 200 OK if request is allowed
     * - 429 Too Many Requests if rate limit is exceeded
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        }

        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
        response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        return false;
    }
}