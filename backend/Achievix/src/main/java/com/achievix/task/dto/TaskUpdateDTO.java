package com.achievix.task.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskUpdateDTO {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Goal ID cannot be null")
    private Long goalId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private Boolean completed;

    @FutureOrPresent(message = "Deadline must be in the present or future")
    private LocalDate deadline;

    @NotNull(message = "CreatedAt cannot be null")
    private LocalDateTime createdAt;
}
