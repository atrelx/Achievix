package com.achievix.task;

import com.achievix.exception.ResourceNotFoundException;
import com.achievix.exception.UnauthorizedAccessException;
import com.achievix.goal.Goal;
import com.achievix.goal.GoalRepository;
import com.achievix.kafka.KafkaProducerService;
import com.achievix.task.dto.TaskCompletedEventDTO;
import com.achievix.task.dto.TaskDTO;
import com.achievix.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private TaskService taskService;

    private Goal goal;
    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        goal = new Goal();
        goal.setId(1L);
        goal.setUser(new User());
        goal.getUser().setId(1L);

        task = new Task();
        task.setId(1L);
        task.setGoal(goal);
        task.setTitle("Test Task");
        task.setCompleted(false);

        taskDTO = new TaskDTO();
        taskDTO.setCompleted(false);
        taskDTO.setGoalId(1L);
        taskDTO.setTitle("Test Task");

        // Mocking the SecurityContext so that user is logged in
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("1");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldThrowExceptionWhenAccessingTasksForUnauthorizedGoal() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        goal.getUser().setId(2L);

        assertThatThrownBy(() -> taskService.getTasksByGoal(1L))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessage("You do not have permission to access this goal");
    }

    @Test
    void shouldCompleteTaskSuccessfullyWithoutKafka() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        taskDTO.setCompleted(true);
        when(taskMapper.toDTO(any(Task.class))).thenReturn(taskDTO);

        doNothing().when(kafkaProducerService).sendTaskCompletedEvent(any(TaskCompletedEventDTO.class));
        TaskDTO completedTask = taskService.completeTask(1L);

        assertThat(completedTask.getCompleted()).isTrue();
        verify(taskRepository).save(task);
        verify(kafkaProducerService).sendTaskCompletedEvent(any(TaskCompletedEventDTO.class));
    }

    @Test
    void shouldThrowExceptionWhenCompletingTaskForUnauthorizedGoal() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        goal.getUser().setId(2L);

        assertThatThrownBy(() -> taskService.completeTask(1L))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessage("You do not have permission to complete this task");
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.completeTask(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task with ID 1 not found");
    }
}