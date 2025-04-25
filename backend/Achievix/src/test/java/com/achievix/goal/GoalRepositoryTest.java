package com.achievix.goal;

import org.assertj.core.api.Assertions;
import com.achievix.user.User;
import com.achievix.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GoalRepositoryTest {

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
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Test
    void shouldSaveAndFindGoalsByUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user = userRepository.save(user);

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTitle("Read 10 books");
        goal.setTargetValue(10);
        goal.setCurrentValue(0);
        goalRepository.save(goal);

        List<Goal> goals = goalRepository.findByUser(user);
        Assertions.assertThat(goals).hasSize(1);
        assertThat(goals.get(0).getTitle()).isEqualTo("Read 10 books");
        assertThat(goals.get(0).getTargetValue()).isEqualTo(10);
    }

    @Test
    void shouldSaveAndRetrieveGoalWithDeadline() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user = userRepository.save(user);

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTitle("Read 10 books");
        goal.setTargetValue(10);
        goal.setCurrentValue(0);
        goal.setDeadline(LocalDate.of(2025, 12, 31));
        goalRepository.save(goal);

        List<Goal> goals = goalRepository.findByUser(user);
        Assertions.assertThat(goals).hasSize(1);
        assertThat(goals.get(0).getDeadline()).isEqualTo(LocalDate.of(2025, 12, 31));
    }

    @Test
    void shouldReturnEmptyListIfNoGoalsForUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user = userRepository.save(user);

        List<Goal> goals = goalRepository.findByUser(user);
        Assertions.assertThat(goals).isEmpty();
    }
}