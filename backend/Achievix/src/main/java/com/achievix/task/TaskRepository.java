package com.achievix.task;

import com.achievix.goal.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByGoal(Goal goal);
}
