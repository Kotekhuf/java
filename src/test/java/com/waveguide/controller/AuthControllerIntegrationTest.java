package com.waveguide.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waveguide.model.dto.request.LoginRequest;
import com.waveguide.model.dto.request.UserRegistrationRequest;
import com.waveguide.model.dto.response.AuthResponse;
import com.waveguide.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void register_WithValidRequest_ShouldReturnToken() throws Exception {
        // Arrange
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Password123")
                .build();

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").isNumber())
                .andExpect(jsonPath("$.username").value(request.getUsername()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andReturn();

        // Verify user is created in database
        assertTrue(userRepository.existsByUsername(request.getUsername()));
        assertTrue(userRepository.existsByEmail(request.getEmail()));

        // Verify response can be deserialized
        AuthResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthResponse.class
        );
        assertNotNull(response.getToken());
    }

    @Test
    void register_WithExistingUsername_ShouldReturnConflict() throws Exception {
        // Arrange
        UserRegistrationRequest request1 = UserRegistrationRequest.builder()
                .username("existinguser")
                .email("user1@example.com")
                .password("Password123")
                .build();

        UserRegistrationRequest request2 = UserRegistrationRequest.builder()
                .username("existinguser")
                .email("user2@example.com")
                .password("Password123")
                .build();

        // Register first user
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Act & Assert for duplicate username
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void register_WithExistingEmail_ShouldReturnConflict() throws Exception {
        // Arrange
        UserRegistrationRequest request1 = UserRegistrationRequest.builder()
                .username("user1")
                .email("duplicate@example.com")
                .password("Password123")
                .build();

        UserRegistrationRequest request2 = UserRegistrationRequest.builder()
                .username("user2")
                .email("duplicate@example.com")
                .password("Password123")
                .build();

        // Register first user
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Act & Assert for duplicate email
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Arrange
        UserRegistrationRequest registerRequest = UserRegistrationRequest.builder()
                .username("loginuser")
                .email("login@example.com")
                .password("Password123")
                .build();

        // Register user first
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = LoginRequest.builder()
                .email("login@example.com")
                .password("Password123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").isNumber())
                .andExpect(jsonPath("$.username").value(registerRequest.getUsername()))
                .andExpect(jsonPath("$.email").value(registerRequest.getEmail()));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
                .email("nonexistent@example.com")
                .password("WrongPassword123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
    
    @Test
    void logout_WithValidToken_ShouldReturnNoContent() throws Exception {
        // Arrange
        // Register and login a user to get a token
        UserRegistrationRequest registerRequest = UserRegistrationRequest.builder()
                .username("logoutuser")
                .email("logout@example.com")
                .password("Password123")
                .build();

        // Register user
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
                
        // Login user
        LoginRequest loginRequest = LoginRequest.builder()
                .email("logout@example.com")
                .password("Password123")
                .build();
                
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
                
        AuthResponse authResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                AuthResponse.class
        );
        
        String token = authResponse.getToken();
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
                
        // Try to access a protected endpoint with the same token (should fail)
        // This would require implementing a test for a protected endpoint
    }
}