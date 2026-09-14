package com.vehicle.backend.modules.auth.service;

import com.vehicle.backend.modules.auth.dto.JwtAuthenticationResponse;
import com.vehicle.backend.modules.auth.dto.LoginRequest;
import com.vehicle.backend.modules.auth.dto.SignUpRequest;
import com.vehicle.backend.modules.auth.dto.SocialLoginRequest;
import com.vehicle.backend.security.oauth2.AuthProvider;

public interface AuthService {
    JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest);
    String registerUser(SignUpRequest signUpRequest);
    JwtAuthenticationResponse authenticateSocialUser(AuthProvider provider, SocialLoginRequest request);
}
