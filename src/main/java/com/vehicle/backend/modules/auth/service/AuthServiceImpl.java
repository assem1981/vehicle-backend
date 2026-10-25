package com.vehicle.backend.modules.auth.service;

import com.vehicle.backend.config.ActiveMQConfig;
import com.vehicle.backend.modules.auth.dto.JwtAuthenticationResponse;
import com.vehicle.backend.modules.auth.dto.LoginRequest;
import com.vehicle.backend.modules.auth.dto.SignUpRequest;
import com.vehicle.backend.modules.auth.dto.SocialLoginRequest;
import com.vehicle.backend.modules.user.entity.HostApprovalStatus;
import com.vehicle.backend.modules.user.entity.Role;
import com.vehicle.backend.modules.user.entity.User;
import com.vehicle.backend.modules.user.repository.UserRepository;
import com.vehicle.backend.security.JwtTokenProvider;
import com.vehicle.backend.security.UserPrincipal;
import com.vehicle.backend.security.oauth2.AuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final JmsTemplate jmsTemplate;

    @Override
    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        log.info("User logged in successfully: {}", loginRequest.getEmail());

        // Send audit event via ActiveMQ
        jmsTemplate.convertAndSend(ActiveMQConfig.AUDIT_QUEUE, Map.of(
                "action", "USER_LOGIN",
                "email", loginRequest.getEmail(),
                "status", "SUCCESS"
        ));

        return new JwtAuthenticationResponse(jwt);
    }

    @Override
    public String registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        Set<Role> roles = new HashSet<>();
        boolean isHostRequest = false;
        if (signUpRequest.getRoles() == null || signUpRequest.getRoles().isEmpty()) {
            roles.add(Role.ROLE_USER);
        } else {
            signUpRequest.getRoles().forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ADMIN":
                    case "ROLE_ADMIN":
                        roles.add(Role.ROLE_ADMIN);
                        break;
                    case "HOST":
                    case "ROLE_HOST":
                        roles.add(Role.ROLE_HOST);
                        break;
                    default:
                        roles.add(Role.ROLE_USER);
                }
            });
            isHostRequest = roles.contains(Role.ROLE_HOST);
        }

        // Host registrations require admin approval — account starts inactive
        boolean accountActive = !isHostRequest;
        HostApprovalStatus approvalStatus = isHostRequest
                ? HostApprovalStatus.PENDING
                : HostApprovalStatus.NONE;

        boolean emailUpdatesOptIn = Boolean.TRUE.equals(signUpRequest.getEmailUpdates());
        boolean agreeTermsOptIn = signUpRequest.getAgreeTerms() == null || Boolean.TRUE.equals(signUpRequest.getAgreeTerms());

        User user = User.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .phoneNumber(signUpRequest.getPhoneNumber())
                .roles(roles)
                .provider(AuthProvider.LOCAL)
                .active(accountActive)
                .hostApprovalStatus(approvalStatus)
                .emailUpdates(emailUpdatesOptIn)
                .agreedToTerms(agreeTermsOptIn)
                .termsAgreedAt(agreeTermsOptIn ? LocalDateTime.now() : null)
                .build();

        userRepository.save(user);

        if (isHostRequest) {
            // Notify admins about pending host application
            jmsTemplate.convertAndSend(ActiveMQConfig.NOTIFICATION_QUEUE, Map.of(
                    "type", "HOST_APPLICATION_PENDING",
                    "applicantEmail", user.getEmail(),
                    "applicantName", user.getFirstName() + " " + user.getLastName(),
                    "title", "New Host Application Pending",
                    "message", user.getFirstName() + " " + user.getLastName() + " has applied to become a host and is awaiting approval."
            ));
            return "Host registration submitted successfully! Your account is pending admin approval. You will be notified once approved.";
        }

        // Trigger welcome notification via ActiveMQ
        jmsTemplate.convertAndSend(ActiveMQConfig.NOTIFICATION_QUEUE, Map.of(
                "recipientEmail", user.getEmail(),
                "title", "Welcome to Vehicle Platform",
                "message", "Your account has been successfully created!"
        ));

        return "User registered successfully!";
    }

    @Override
    public JwtAuthenticationResponse authenticateSocialUser(AuthProvider provider, SocialLoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required for social authentication");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .map(existingUser -> {
                    if (existingUser.getProvider() != provider) {
                        existingUser.setProvider(provider);
                    }
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(request.getEmail())
                            .firstName(request.getFirstName() != null ? request.getFirstName() : provider.name() + "User")
                            .lastName(request.getLastName() != null ? request.getLastName() : "")
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .roles(Set.of(Role.ROLE_USER))
                            .provider(provider)
                            .active(true)
                            .build();
                    return userRepository.save(newUser);
                });

        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        log.info("User logged in successfully via {}: {}", provider, user.getEmail());

        try {
            jmsTemplate.convertAndSend(ActiveMQConfig.AUDIT_QUEUE, Map.of(
                    "action", "OAUTH2_SOCIAL_LOGIN",
                    "provider", provider.name(),
                    "email", user.getEmail(),
                    "status", "SUCCESS"
            ));
        } catch (Exception e) {
            System.err.println("ActiveMQ social login audit notice (non-blocking): " + e.getMessage());
        }

        return new JwtAuthenticationResponse(jwt);
    }
}
