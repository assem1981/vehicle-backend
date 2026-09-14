package com.vehicle.backend.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocialLoginRequest {

    @NotBlank
    private String token; // OAuth2 ID token / Access token or Auth code from Google/Apple SDK

    private String email;
    private String firstName;
    private String lastName;
}
