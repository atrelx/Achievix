package com.achievix.dashboard.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class DashboardDTO {
    // Global statistics
    private int completedGoals;
    private int completedTasks;
    private int activeGoals;
    private int activeTasks;

    // statistics by period
    private Map<String, Integer> goalsCompletedByPeriod; // ex. {"2025-05": 2, "2025-06": 1}
    private Map<String, Integer> tasksCompletedByPeriod;

    // completed goals and tasks
    private List<ArchiveEntry> completedGoalsArchive;
    private List<ArchiveEntry> completedTasksArchive;

    // completed goals and tasks by period in format Title: Date
    @Data
    public static class ArchiveEntry {
        private String title;
        private LocalDateTime completedAt;
    }
}
