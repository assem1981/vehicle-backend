package com.vehicle.backend.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.backend.modules.auth.dto.SocialLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:oauth2testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.activemq.broker-url=vm://localhost?broker.persistent=false"
})
class OAuth2AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/oauth2/google - Should authenticate Google user and return JWT token")
    void testGoogleOAuth2LoginSuccess() throws Exception {
        SocialLoginRequest request = new SocialLoginRequest();
        request.setToken("mock-google-id-token");
        request.setEmail("user.google@gmail.com");
        request.setFirstName("Google");
        request.setLastName("User");

        mockMvc.perform(post("/api/auth/oauth2/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /api/auth/oauth2/apple - Should authenticate Apple user and return JWT token")
    void testAppleOAuth2LoginSuccess() throws Exception {
        SocialLoginRequest request = new SocialLoginRequest();
        request.setToken("mock-apple-id-token");
        request.setEmail("user.apple@privaterelay.appleid.com");
        request.setFirstName("Apple");
        request.setLastName("User");

        mockMvc.perform(post("/api/auth/oauth2/apple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }
}
