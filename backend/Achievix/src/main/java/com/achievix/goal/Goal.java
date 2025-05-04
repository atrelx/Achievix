package com.achievix.goal;

import com.achievix.task.Task;
import com.achievix.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "goals")
@Data
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Target value is required")
    @PositiveOrZero(message = "Target value must be positive or zero")
    @Column(name = "target_value", nullable = false)
    private Integer targetValue;

    @NotNull(message = "Current value is required")
    @Column(name = "current_value", nullable = false)
    @PositiveOrZero(message = "Current value must be positive or zero")
    private Integer currentValue;

    @FutureOrPresent(message = "Deadline must be in the present or future")
    private LocalDate deadline;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (currentValue == null) {
            currentValue = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
