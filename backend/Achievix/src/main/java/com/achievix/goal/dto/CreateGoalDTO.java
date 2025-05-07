package com.achievix.goal.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateGoalDTO {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @FutureOrPresent(message = "Deadline must be in the present or future")
    private LocalDate deadline;
}
