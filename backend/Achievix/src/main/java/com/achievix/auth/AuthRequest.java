package com.achievix.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AuthRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
