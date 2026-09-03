package com.enterprise.smart_feedback_engine.service;

import com.enterprise.smart_feedback_engine.dto.AuthResponseDto;
import com.enterprise.smart_feedback_engine.dto.LoginRequestDto;
import com.enterprise.smart_feedback_engine.entity.Role;
import com.enterprise.smart_feedback_engine.entity.User;
import com.enterprise.smart_feedback_engine.repository.UserRepository;
import com.enterprise.smart_feedback_engine.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Giris yapildiginda kullanici dogrulanmali ve JWT token donmeli")
    void shouldAuthenticateAndReturnJwtToken() {
        // Given
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("iclal");
        request.setPassword("password123");

        // Role enum tanımına göre (ROLE_USER veya USER)
        Role userRole = Role.values()[0];

        User user = User.builder()
                .id(1L)
                .username("iclal")
                .password("encodedPassword")
                .role(userRole)
                .build();

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "iclal",
                "encodedPassword",
                Collections.emptyList()
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByUsername("iclal")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(), any())).thenReturn("mocked-jwt-token");

        // When
        AuthResponseDto response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername("iclal");
        verify(jwtService, times(1)).generateToken(any(), any());
    }
}