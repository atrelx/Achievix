package com.achievix.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskDTO {

    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Goal ID cannot be null")
    private Long goalId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private Boolean completed;
    private LocalDateTime completedAt;
    private LocalDate deadline;

    @NotNull(message = "CreatedAt cannot be null")
    private LocalDateTime createdAt;
}
