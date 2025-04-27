package com.achievix.user;

import com.achievix.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    public Map<String, String> register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user = userRepository.save(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", jwtUtil.generateAccessToken(user.getId()));
        tokens.put("refreshToken", jwtUtil.generateRefreshToken(user.getId()));
        return tokens;
    }

    public Map<String, String> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getId().toString(), password)
            );
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", jwtUtil.generateAccessToken(user.getId()));
            tokens.put("refreshToken", jwtUtil.generateRefreshToken(user.getId()));
            return tokens;
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    public Map<String, String> refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", jwtUtil.generateAccessToken(userId));
        tokens.put("refreshToken", jwtUtil.generateRefreshToken(userId));
        return tokens;
    }

    public User getCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}