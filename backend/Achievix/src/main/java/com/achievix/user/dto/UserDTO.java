package com.achievix.user.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;

    public UserDTO(Long id, String email) {
        this.id = id;
        this.email = email;
    }

}
