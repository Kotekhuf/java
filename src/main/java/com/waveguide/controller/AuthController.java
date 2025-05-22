package com.waveguide.controller;

import com.waveguide.model.dto.request.LoginRequest;
import com.waveguide.model.dto.request.UserRegistrationRequest;
import com.waveguide.model.dto.response.AuthResponse;
import com.waveguide.model.entity.User;
import com.waveguide.security.CurrentUser;
import com.waveguide.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication operations")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided credentials")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout a user", description = "Invalidates the user's JWT token")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @CurrentUser User currentUser
    ) {
        // Extract token from Authorization header
        String token = authHeader.substring(7);
        authService.logout(token, currentUser);
        return ResponseEntity.noContent().build();
    }
}