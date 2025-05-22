package com.waveguide.service;

import com.waveguide.model.entity.Log;
import com.waveguide.model.entity.User;
import com.waveguide.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {
    
    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUserAction(User user, String action, String details) {
        try {
            Log logEntry = Log.builder()
                    .user(user)
                    .action(action)
                    .details(details)
                    .build();
            
            logRepository.save(logEntry);
            log.info("Logged action: {} for user: {}, details: {}", action, user.getUsername(), details);
        } catch (Exception e) {
            // Make sure an error in logging doesn't affect the main transaction
            log.error("Error logging user action", e);
        }
    }
}