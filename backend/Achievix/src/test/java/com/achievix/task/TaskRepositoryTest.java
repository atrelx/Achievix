package com.achievix.task;

import com.achievix.goal.Goal;
import com.achievix.goal.GoalRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRepositoryTest {

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

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldSaveAndFindTasksByGoal() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user = userRepository.save(user);

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTitle("Read 10 books");
        goal.setTargetValue(10);
        goal = goalRepository.save(goal);

        Task task = new Task();
        task.setGoal(goal);
        task.setTitle("Read book 1");
        task.setCompleted(false);
        taskRepository.save(task);

        List<Task> tasks = taskRepository.findByGoal(goal);
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Read book 1");
        assertThat(tasks.get(0).getCompleted()).isFalse();
    }

    @Test
    void shouldReturnEmptyListIfNoTasksForGoal() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user = userRepository.save(user);

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTitle("Read 10 books");
        goal.setTargetValue(10);
        goal = goalRepository.save(goal);

        List<Task> tasks = taskRepository.findByGoal(goal);
        assertThat(tasks).isEmpty();
    }
}