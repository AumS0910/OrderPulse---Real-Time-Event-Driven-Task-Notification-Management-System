package org.orderpulse.orderpulsebackend.service;

import lombok.RequiredArgsConstructor;
import org.orderpulse.orderpulsebackend.dto.AuthenticationRequest;
import org.orderpulse.orderpulsebackend.dto.AuthenticationResponse;
import org.orderpulse.orderpulsebackend.dto.RegisterRequest;
import org.orderpulse.orderpulsebackend.entity.Role;
import org.orderpulse.orderpulsebackend.entity.User;
import org.orderpulse.orderpulsebackend.repository.UserRepository;
import org.orderpulse.orderpulsebackend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for user authentication and registration.
 * Handles user registration, login, and JWT token generation.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user in the system.
     * @param request Registration details including username, email, and password
     * @return JWT token for the newly registered user
     */
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Authenticates an existing user.
     * @param request Authentication credentials (username and password)
     * @return JWT token if authentication is successful
     * @throws org.springframework.security.core.AuthenticationException if authentication fails
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}