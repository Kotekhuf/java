package com.waveguide.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waveguide.model.dto.response.ErrorResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${app.rate-limiting.requests-per-hour:100}")
    private int requestsPerHour;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Skip rate limiting for OPTIONS requests (pre-flight CORS)
        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Get client IP for rate limiting
        String clientIp = getClientIp(request);
        
        // Get or create rate limiter for this IP
        Bucket bucket = buckets.computeIfAbsent(clientIp, this::createNewBucket);
        
        // Try to consume a token
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(HttpStatus.TOO_MANY_REQUESTS.value())
                    .message("Rate limit exceeded. Try again later.")
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();
            
            objectMapper.findAndRegisterModules();
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    private Bucket createNewBucket(String clientIp) {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(requestsPerHour, Refill.intervally(requestsPerHour, Duration.ofHours(1))))
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}