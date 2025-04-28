package com.achievix.task.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCompletedEventDTO {
    private Long taskId;
    private String title;
    private Long goalId;
    private LocalDateTime completedAt;
}