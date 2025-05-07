package com.achievix.goal;

import com.achievix.exception.ResourceNotFoundException;
import com.achievix.exception.UnauthorizedAccessException;
import com.achievix.goal.dto.CreateGoalDTO;
import com.achievix.goal.dto.GoalDTO;
import com.achievix.goal.dto.GoalDetailsDTO;
import com.achievix.kafka.KafkaProducerService;
import com.achievix.sendgrid.dto.EmailNotificationDTO;
import com.achievix.user.User;
import com.achievix.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final KafkaProducerService kafkaProducerService;

    public GoalService(GoalRepository goalRepository,
                       UserRepository userRepository,
                       GoalMapper goalMapper,
                       KafkaProducerService kafkaProducerService) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.goalMapper = goalMapper;
        this.kafkaProducerService = kafkaProducerService;
    }

    public List<GoalDTO> getUserGoals() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Goal> goals = goalRepository.findByUserId(Long.parseLong(userId));
        return goals.stream().map(goalMapper::toDTO).collect(Collectors.toList());
    }

    public GoalDetailsDTO getGoalDetailsById(Long goalId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal with ID " + goalId + " not found"));

        if (!goal.getUser().getId().equals(Long.parseLong(userId))) {
            throw new UnauthorizedAccessException("You do not have permission to access this goal");
        }

        return goalMapper.toDetailsDTO(goal);
    }

    public GoalDTO createGoal(CreateGoalDTO createGoalDTO) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        Goal goal = goalMapper.toEntity(createGoalDTO);
        goal.setUser(user);
        goal.setCurrentValue(0);
        goal.setTargetValue(0);

        EmailNotificationDTO emailNotification = new EmailNotificationDTO();
        emailNotification.setToEmail(user.getEmail());
        emailNotification.setSubject("New Goal Created");
        emailNotification.setBody("You have created a new goal: " + goal.getTitle());
        kafkaProducerService.sendEmailNotification(emailNotification);

        Goal savedGoal = goalRepository.save(goal);
        return goalMapper.toDTO(savedGoal);
    }

    public GoalDTO updateGoal(Long goalId, GoalDTO goalDTO) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal with ID " + goalId + " not found"));

        if (!goal.getUser().getId().equals(Long.parseLong(userId))) {
            throw new UnauthorizedAccessException("You do not have permission to update this goal");
        }

        goal.setTitle(goalDTO.getTitle());
        goal.setTargetValue(goalDTO.getTargetValue());
        goal.setCurrentValue(goalDTO.getCurrentValue());
        goal.setDeadline(goalDTO.getDeadline());

        Goal updatedGoal = goalRepository.save(goal);
        return goalMapper.toDTO(updatedGoal);
    }

    public void deleteGoal(Long goalId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal with ID " + goalId + " not found"));

        if (!goal.getUser().getId().equals(Long.parseLong(userId))) {
            throw new UnauthorizedAccessException("You do not have permission to delete this goal");
        }

        goalRepository.delete(goal);
    }
}
