package com.achievix.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("achievix")
            .withUsername("root")
            .withPassword("rootpassword")
            .withInitScript("init.sql")
            .withStartupTimeoutSeconds(120)
            .withEnv("MYSQL_LOG_CONSOLE", "true");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        Assertions.assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getPasswordHash()).isEqualTo("hashedPassword");
    }

    @Test
    void shouldReturnEmptyIfEmailNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        Assertions.assertThat(foundUser).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenSavingDuplicateEmail() {
        User user1 = new User();
        user1.setEmail("test@example.com");
        user1.setPasswordHash("hashedPassword");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("test@example.com");
        user2.setPasswordHash("otherPassword");

        assertThatThrownBy(() -> userRepository.save(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}