package com.achievix.goal.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GoalDTO {
    @NotNull(message = "ID cannot be null")
    @Positive(message = "ID must be a positive number")
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull(message = "Target value cannot be null")
    @Positive(message = "Target value must be a positive number")
    private Integer targetValue;

    @NotNull(message = "Current value cannot be null")
    private Integer currentValue;

    @FutureOrPresent(message = "Deadline must be in the present or future")
    private LocalDate deadline;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
