package com.waveguide.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class JwtTokenBlacklist {

    private final Map<String, Date> blacklistedTokens = new ConcurrentHashMap<>();
    
    public void addToBlacklist(String token, Date expiration) {
        blacklistedTokens.put(token, expiration);
        log.debug("Token added to blacklist, expires at: {}", expiration);
    }
    
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }
    
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        Date now = new Date();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().before(now));
        log.debug("Cleaned up expired tokens from blacklist");
    }
}