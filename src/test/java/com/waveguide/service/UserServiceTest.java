package com.waveguide.service;

import com.waveguide.exception.UserAlreadyExistsException;
import com.waveguide.model.dto.request.UserRegistrationRequest;
import com.waveguide.model.entity.User;
import com.waveguide.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private LogService logService;
    
    @InjectMocks
    private UserService userService;
    
    private UserRegistrationRequest validRequest;
    
    @BeforeEach
    void setUp() {
        validRequest = UserRegistrationRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Password123")
                .build();
        
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    }
    
    @Test
    void registerUser_WithValidRequest_ShouldReturnUser() {
        // Arrange
        when(userRepository.existsByUsername(validRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        
        User savedUser = User.builder()
                .username(validRequest.getUsername())
                .email(validRequest.getEmail())
                .passwordHash("encodedPassword")
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // Act
        User result = userService.registerUser(validRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(validRequest.getUsername(), result.getUsername());
        assertEquals(validRequest.getEmail(), result.getEmail());
        assertEquals("encodedPassword", result.getPasswordHash());
        
        verify(userRepository).existsByUsername(validRequest.getUsername());
        verify(userRepository).existsByEmail(validRequest.getEmail());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(validRequest.getPassword());
        verify(logService).logUserAction(any(User.class), eq("REGISTRATION"), anyString());
    }
    
    @Test
    void registerUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername(validRequest.getUsername())).thenReturn(true);
        
        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(validRequest);
        });
        
        verify(userRepository).existsByUsername(validRequest.getUsername());
        verify(userRepository, never()).existsByEmail(validRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername(validRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);
        
        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(validRequest);
        });
        
        verify(userRepository).existsByUsername(validRequest.getUsername());
        verify(userRepository).existsByEmail(validRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void getUserByEmail_WithExistingEmail_ShouldReturnUser() {
        // Arrange
        String email = "test@example.com";
        User expectedUser = User.builder()
                .username("testuser")
                .email(email)
                .passwordHash("encodedPassword")
                .build();
        
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(expectedUser));
        
        // Act
        java.util.Optional<User> result = userService.getUserByEmail(email);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        verify(userRepository).findByEmail(email);
    }
    
    @Test
    void getUserByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());
        
        // Act
        java.util.Optional<User> result = userService.getUserByEmail(email);
        
        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail(email);
    }
}