package com.achievix.dashboard;

import com.achievix.dashboard.dto.DashboardDTO;
import com.achievix.dashboard.dto.DashboardDTO.ArchiveEntry;
import com.achievix.goal.Goal;
import com.achievix.goal.GoalRepository;
import com.achievix.task.Task;
import com.achievix.task.TaskRepository;
import com.achievix.user.User;
import com.achievix.user.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;

    public DashboardService(UserRepository userRepository, GoalRepository goalRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
        this.taskRepository = taskRepository;
    }

    public DashboardDTO getDashboard(String periodType) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        DashboardDTO dashboard = new DashboardDTO();

        // global statistics
        dashboard.setCompletedGoals(goalRepository.countCompletedGoalsByUser(user));
        dashboard.setCompletedTasks(taskRepository.countCompletedTasksByUser(user));
        dashboard.setActiveGoals(goalRepository.countActiveGoalsByUser(user));
        dashboard.setActiveTasks(taskRepository.countActiveTasksByUser(user));

        // global statistics by period grouping
        String format = switch (periodType.toLowerCase()) {
            case "day" -> "%Y-%m-%d";
            case "month" -> "%Y-%m";
            case "year" -> "%Y";
            default -> throw new IllegalArgumentException("Invalid period type. Use 'day', 'month', or 'year'.");
        };

        // statistics by period
        dashboard.setGoalsCompletedByPeriod(getGoalsCompletedByPeriod(user, format));
        dashboard.setTasksCompletedByPeriod(getTasksCompletedByPeriod(user, format));

        // archive of completed goals and tasks
        dashboard.setCompletedGoalsArchive(getCompletedGoalsArchive(user));
        dashboard.setCompletedTasksArchive(getCompletedTasksArchive(user));

        return dashboard;
    }

    private Map<String, Integer> getGoalsCompletedByPeriod(User user, String format) {
        List<Object[]> results = goalRepository.countCompletedGoalsByPeriod(user, format);
        return getStringIntegerMap(results);
    }

    private Map<String, Integer> getTasksCompletedByPeriod(User user, String format) {
        List<Object[]> results = taskRepository.countCompletedTasksByPeriod(user, format);
        return getStringIntegerMap(results);
    }

    @NotNull
    private Map<String, Integer> getStringIntegerMap(List<Object[]> results) {
        Map<String, Integer> periodMap = new HashMap<>();
        for (Object[] result : results) {
            String period = (String) result[0];
            Long count = (Long) result[1];
            periodMap.put(period, count.intValue());
        }
        return periodMap;
    }

    private List<ArchiveEntry> getCompletedGoalsArchive(User user) {
        List<Goal> completedGoals = goalRepository.findCompletedGoalsByUser(user);
        return completedGoals.stream()
                .map(goal -> {
                    ArchiveEntry entry = new ArchiveEntry();
                    entry.setTitle(goal.getTitle());
                    entry.setCompletedAt(goal.getUpdatedAt());
                    return entry;
                })
                .collect(Collectors.toList());
    }

    private List<ArchiveEntry> getCompletedTasksArchive(User user) {
        List<Task> completedTasks = taskRepository.findCompletedTasksByUser(user);
        return completedTasks.stream()
                .map(task -> {
                    ArchiveEntry entry = new ArchiveEntry();
                    entry.setTitle(task.getTitle());
                    entry.setCompletedAt(task.getCompletedAt());
                    return entry;
                })
                .collect(Collectors.toList());
    }
}