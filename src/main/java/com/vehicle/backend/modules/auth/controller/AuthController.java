package com.vehicle.backend.modules.auth.controller;

import com.vehicle.backend.modules.auth.dto.JwtAuthenticationResponse;
import com.vehicle.backend.modules.auth.dto.LoginRequest;
import com.vehicle.backend.modules.auth.dto.SignUpRequest;
import com.vehicle.backend.modules.auth.dto.SocialLoginRequest;
import com.vehicle.backend.modules.auth.service.AuthService;
import com.vehicle.backend.security.oauth2.AuthProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getAuthConfig() {
        return ResponseEntity.ok(Map.of(
            "googleClientId", googleClientId != null ? googleClientId : ""
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authService.registerUser(signUpRequest));
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<JwtAuthenticationResponse> googleLogin(@Valid @RequestBody SocialLoginRequest socialLoginRequest) {
        return ResponseEntity.ok(authService.authenticateSocialUser(AuthProvider.GOOGLE, socialLoginRequest));
    }

    @PostMapping("/oauth2/apple")
    public ResponseEntity<JwtAuthenticationResponse> appleLogin(@Valid @RequestBody SocialLoginRequest socialLoginRequest) {
        return ResponseEntity.ok(authService.authenticateSocialUser(AuthProvider.APPLE, socialLoginRequest));
    }
}
