package org.example.achievix.goal;

import jakarta.persistence.*;
import lombok.Data;
import org.example.achievix.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Data
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "target_value", nullable = false)
    private Integer targetValue;

    @Column(name = "current_value", nullable = false)
    private Integer currentValue = 0;

    @Column
    private LocalDate deadline;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
