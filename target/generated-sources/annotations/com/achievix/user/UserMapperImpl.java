package com.achievix.user;

import com.achievix.user.dto.UserDTO;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-01T20:02:08+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String email = null;
        LocalDateTime createdAt = null;

        id = user.getId();
        email = user.getEmail();
        createdAt = user.getCreatedAt();

        UserDTO userDTO = new UserDTO( id, email, createdAt );

        return userDTO;
    }
}
