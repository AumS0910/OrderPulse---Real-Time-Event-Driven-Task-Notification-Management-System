package org.orderpulse.orderpulsebackend.config;

import lombok.RequiredArgsConstructor;
import org.orderpulse.orderpulsebackend.interceptor.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class that customizes Spring MVC behavior.
 * Registers interceptors and configures web-related features.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    /**
     * Registers the rate limit interceptor for API endpoints.
     * Applies rate limiting to all /api/** paths except:
     * - Authentication endpoints (/api/auth/**)
     * - Swagger UI and OpenAPI documentation
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**");
    }
}