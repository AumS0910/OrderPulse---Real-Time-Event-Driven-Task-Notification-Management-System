package org.orderpulse.orderpulsebackend.controller;

import lombok.RequiredArgsConstructor;
import org.orderpulse.orderpulsebackend.dto.AuthenticationRequest;
import org.orderpulse.orderpulsebackend.dto.AuthenticationResponse;
import org.orderpulse.orderpulsebackend.dto.RegisterRequest;
import org.orderpulse.orderpulsebackend.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication-related endpoints.
 * Provides endpoints for user registration and authentication.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Handles user registration requests.
     * Creates a new user account and returns a JWT token.
     *
     * @param request Contains user registration details (username, email, password)
     * @return JWT token wrapped in ResponseEntity
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    /**
     * Handles user authentication requests.
     * Validates credentials and returns a JWT token.
     *
     * @param request Contains user credentials (username, password)
     * @return JWT token wrapped in ResponseEntity
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}