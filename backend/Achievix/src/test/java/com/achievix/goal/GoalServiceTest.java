package com.achievix.goal;

import com.achievix.exception.ResourceNotFoundException;
import com.achievix.exception.UnauthorizedAccessException;
import com.achievix.goal.dto.CreateGoalDTO;
import com.achievix.goal.dto.GoalDTO;
import com.achievix.kafka.KafkaProducerService;
import com.achievix.sendgrid.dto.EmailNotificationDTO;
import com.achievix.user.User;
import com.achievix.user.UserRepository;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private GoalMapper goalMapper;

    @InjectMocks
    private GoalService goalService;

    private User user;
    private Goal goal;
    private GoalDTO goalDTO;
    private CreateGoalDTO createGoalDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        goal = new Goal();
        goal.setId(1L);
        goal.setUser(user);
        goal.setTitle("Test Goal");

        goalDTO = new GoalDTO();
        goalDTO.setTitle("Test Goal");

        createGoalDTO = new CreateGoalDTO();
        createGoalDTO.setTitle("Test Goal");
        createGoalDTO.setDeadline(LocalDate.now().plusDays(30));


        // Mocking the SecurityContext so that user is logged in
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("1");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCreateGoalSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(goalMapper.toEntity(any(GoalDTO.class))).thenReturn(goal);
        when(goalMapper.toDTO(any(Goal.class))).thenReturn(goalDTO);

        // Mocking the Kafka producer service to avoid sending actual emails
        doNothing().when(kafkaProducerService).sendEmailNotification(any(EmailNotificationDTO.class));
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);


        GoalDTO createdGoal = goalService.createGoal(createGoalDTO);

        assertThat(createdGoal.getTitle()).isEqualTo("Test Goal");
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForCreateGoal() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.createGoal(createGoalDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with ID 1 not found");
    }

    @Test
    void shouldUpdateGoalSuccessfully() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(goalMapper.toDTO(goal)).thenReturn(goalDTO);

        GoalDTO updatedGoal = goalService.updateGoal(1L, goalDTO);

        assertThat(updatedGoal).isNotNull();
        assertThat(updatedGoal.getTitle()).isEqualTo("Test Goal");

        verify(goalRepository).save(goal);
        verify(goalMapper).toDTO(goal);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUnauthorizedGoal() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        goal.getUser().setId(2L);

        assertThatThrownBy(() -> goalService.updateGoal(1L, goalDTO))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessage("You do not have permission to update this goal");
    }

    @Test
    void shouldThrowExceptionWhenGoalNotFoundForUpdate() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.updateGoal(1L, goalDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Goal with ID 1 not found");
    }
}