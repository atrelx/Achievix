package com.achievix.task;

import com.achievix.exception.ResourceNotFoundException;
import com.achievix.exception.UnauthorizedAccessException;
import com.achievix.kafka.KafkaProducerService;
import com.achievix.task.dto.TaskCompletedEventDTO;
import com.achievix.task.dto.TaskDTO;
import com.achievix.goal.Goal;
import com.achievix.goal.GoalRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    private final TaskMapper taskMapper;
    private final KafkaProducerService kafkaProducerService;

    public TaskService(TaskRepository taskRepository,
                       GoalRepository goalRepository,
                       TaskMapper taskMapper,
                       KafkaProducerService kafkaProducerService) {
        this.taskRepository = taskRepository;
        this.goalRepository = goalRepository;
        this.taskMapper = taskMapper;
        this.kafkaProducerService = kafkaProducerService;
    }

    public List<TaskDTO> getTasksByGoal(Long goalId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal with ID " + goalId + " not found"));

        if (!goal.getUser().getId().equals(Long.parseLong(userId))) {
            throw new UnauthorizedAccessException("You do not have permission to access this goal");
        }

        List<Task> tasks = taskRepository.findByGoal(goal);
        return tasks.stream().map(taskMapper::toDTO).collect(Collectors.toList());
    }

    public TaskDTO createTask(TaskDTO taskDTO) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Goal goal = goalRepository.findById(taskDTO.getGoalId())
                .orElseThrow(() -> new ResourceNotFoundException("Goal with ID " + taskDTO.getGoalId() + " not found"));

        if (!goal.getUser().getId().equals(Long.parseLong(userId))) {
            throw new UnauthorizedAccessException("You do not have permission to create a task for this goal");
        }

        // Increment the target value of the goal "adding" task to a goal
        goal.setTargetValue(goal.getTargetValue() + 1);
        goalRepository.save(goal);

        Task task = taskMapper.toEntity(taskDTO);
        task.setGoal(goal);
        task.setCompleted(false);

        Task savedTask = taskRepository.save(task);
        return taskMapper.toDTO(savedTask);
    }

    public TaskDTO completeTask(Long taskId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + taskId + " not found"));

        if (!task.getGoal().getUser().getId().equals(Long.parseLong(userId))) {
            throw new UnauthorizedAccessException("You do not have permission to complete this task");
        }

        task.setCompleted(true);
        Task updatedTask = taskRepository.save(task);

        // Increment the goal's current value "completing" task for a goal
        Goal goal = task.getGoal();
        goal.setCurrentValue(goal.getCurrentValue() + 1);
        goalRepository.save(goal);

        TaskCompletedEventDTO event = new TaskCompletedEventDTO();
        event.setTaskId(updatedTask.getId());
        event.setTitle(updatedTask.getTitle());
        event.setGoalId(updatedTask.getGoal().getId());
        event.setCompletedAt(updatedTask.getCompletedAt());
        kafkaProducerService.sendTaskCompletedEvent(event);

        return taskMapper.toDTO(updatedTask);
    }

    public void deleteTask(Long taskId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + taskId + " not found"));

        if (!task.getGoal().getUser().getId().equals(Long.parseLong(userId))) {
            throw new UnauthorizedAccessException("You do not have permission to delete this task");
        }

        // Decrement the goal's target value "deleting" task for a goal
        Goal goal = task.getGoal();
        goal.setTargetValue(goal.getTargetValue() - 1);
        // Decrement the goal's current value if task is completed
        if (task.getCompleted()) {
            goal.setCurrentValue(goal.getCurrentValue() - 1);
        }
        goalRepository.save(goal);

        taskRepository.delete(task);
    }
}