package com.achievix.user;

import com.achievix.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateAccessToken(1L)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(1L)).thenReturn("refresh-token");

        Map<String, String> tokens = userService.register("test@example.com", "password");

        assertThat(tokens.get("accessToken")).isEqualTo("access-token");
        assertThat(tokens.get("refreshToken")).isEqualTo("refresh-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExistsDuringRegistration() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.register("test@example.com", "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");
    }

    @Test
    void shouldLoginUserSuccessfully() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(jwtUtil.generateAccessToken(1L)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(1L)).thenReturn("refresh-token");

        Map<String, String> tokens = userService.login("test@example.com", "password");

        assertThat(tokens.get("accessToken")).isEqualTo("access-token");
        assertThat(tokens.get("refreshToken")).isEqualTo("refresh-token");
    }

    @Test
    void shouldThrowExceptionWhenLoginFails() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Authentication failed") {});

        assertThatThrownBy(() -> userService.login("test@example.com", "wrong-password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void shouldRefreshTokensSuccessfully() {
        when(jwtUtil.validateToken("refresh-token")).thenReturn(true);
        when(jwtUtil.getUserIdFromToken("refresh-token")).thenReturn(1L);
        when(jwtUtil.generateAccessToken(1L)).thenReturn("new-access-token");
        when(jwtUtil.generateRefreshToken(1L)).thenReturn("new-refresh-token");

        Map<String, String> tokens = userService.refresh("refresh-token");

        assertThat(tokens.get("accessToken")).isEqualTo("new-access-token");
        assertThat(tokens.get("refreshToken")).isEqualTo("new-refresh-token");
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        assertThatThrownBy(() -> userService.refresh("invalid-token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid refresh token");
    }
}