package com.achievix.auth;

import com.achievix.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request, HttpServletResponse response) {
        Map<String, String> tokens = userService.register(request.getEmail(), request.getPassword());
        setTokensInCookies(response, tokens);
        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        Map<String, String> tokens = userService.login(request.getEmail(), request.getPassword());
        setTokensInCookies(response, tokens);
        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Refresh token not found");
        }

        Map<String, String> tokens = userService.refresh(refreshToken);
        setTokensInCookies(response, tokens);
        return ResponseEntity.ok("Token refreshed");
    }

    private void setTokensInCookies(HttpServletResponse response, Map<String, String> tokens) {
        Cookie accessTokenCookie = new Cookie("accessToken", tokens.get("accessToken"));
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60); // 15 minutes
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(refreshTokenCookie);
    }
}

