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

import java.util.*;
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
        Map<String, Integer> periodMap = getStringIntegerMap(results);

        if (!periodMap.isEmpty()) {
            return fillMissingPeriods(periodMap);
        }

        return periodMap;
    }

    private Map<String, Integer> getTasksCompletedByPeriod(User user, String format) {
        List<Object[]> results = taskRepository.countCompletedTasksByPeriod(user, format);
        Map<String, Integer> periodMap = getStringIntegerMap(results);

        if (!periodMap.isEmpty()) {
            return fillMissingPeriods(periodMap);
        }

        return periodMap;
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

    // Fills 0 for missing periods in the map
    private Map<String, Integer> fillMissingPeriods(Map<String, Integer> periodMap) {
        if (periodMap.isEmpty()) return periodMap;

        List<String> periods = new ArrayList<>(periodMap.keySet());
        Collections.sort(periods);

        String firstPeriod = periods.get(0);
        String lastPeriod = periods.get(periods.size() - 1);

        // Determine period format based on string length and structure
        if (firstPeriod.length() == 4) {
            // Year format: "YYYY"
            return fillMissingYears(periodMap, firstPeriod, lastPeriod);
        } else if (firstPeriod.length() == 7) {
            // Month format: "YYYY-MM"
            return fillMissingMonths(periodMap, firstPeriod, lastPeriod);
        } else {
            // Day format: "YYYY-MM-DD"
            return fillMissingDays(periodMap, firstPeriod, lastPeriod);
        }
    }

    private Map<String, Integer> fillMissingYears(Map<String, Integer> periodMap, String firstPeriod, String lastPeriod) {
        int firstYear = Integer.parseInt(firstPeriod);
        int lastYear = Integer.parseInt(lastPeriod);

        Map<String, Integer> result = new LinkedHashMap<>();

        for (int year = firstYear; year <= lastYear; year++) {
            String key = String.valueOf(year);
            result.put(key, periodMap.getOrDefault(key, 0));
        }

        return result;
    }

    private Map<String, Integer> fillMissingMonths(Map<String, Integer> periodMap, String firstPeriod, String lastPeriod) {
        int firstYear = Integer.parseInt(firstPeriod.substring(0, 4));
        int firstMonth = Integer.parseInt(firstPeriod.substring(5, 7));
        int lastYear = Integer.parseInt(lastPeriod.substring(0, 4));
        int lastMonth = Integer.parseInt(lastPeriod.substring(5, 7));

        Map<String, Integer> result = new LinkedHashMap<>();

        int year = firstYear;
        int month = firstMonth;

        while (year < lastYear || (year == lastYear && month <= lastMonth)) {
            String key = String.format("%d-%02d", year, month);
            result.put(key, periodMap.getOrDefault(key, 0));

            month++;
            if (month > 12) {
                month = 1;
                year++;
            }
        }

        return result;
    }

    private Map<String, Integer> fillMissingDays(Map<String, Integer> periodMap, String firstPeriod, String lastPeriod) {
        // Parse the start and end dates
        int firstYear = Integer.parseInt(firstPeriod.substring(0, 4));
        int firstMonth = Integer.parseInt(firstPeriod.substring(5, 7));
        int firstDay = Integer.parseInt(firstPeriod.substring(8, 10));

        int lastYear = Integer.parseInt(lastPeriod.substring(0, 4));
        int lastMonth = Integer.parseInt(lastPeriod.substring(5, 7));
        int lastDay = Integer.parseInt(lastPeriod.substring(8, 10));

        // Create Calendar instances for start and end dates
        Calendar calendar = Calendar.getInstance();
        calendar.set(firstYear, firstMonth - 1, firstDay); // Month is 0-based in Calendar

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(lastYear, lastMonth - 1, lastDay);

        Map<String, Integer> result = new LinkedHashMap<>();

        // Iterate through each day
        while (!calendar.after(endCalendar)) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Convert back to 1-based month
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            String key = String.format("%d-%02d-%02d", year, month, day);
            result.put(key, periodMap.getOrDefault(key, 0));

            // Move to next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return result;
    }
}