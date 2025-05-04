package com.achievix.task.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskCreateDTO {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull(message = "Goal ID cannot be null")
    private Long goalId;

    @FutureOrPresent(message = "Deadline must be in the present or future")
    private LocalDate deadline;
}


