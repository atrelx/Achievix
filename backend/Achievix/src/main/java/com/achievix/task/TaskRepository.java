package com.achievix.task;

import com.achievix.goal.Goal;
import com.achievix.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByGoalId(Long goalId);
    List<Task> findByGoal(Goal goal);
    long countByGoalIdInAndCompleted(List<Long> goalIds, boolean completed);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.goal.user = :user AND t.completed = true")
    int countCompletedTasksByUser(User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.goal.user = :user AND t.completed = false")
    int countActiveTasksByUser(User user);

    @Query("SELECT t FROM Task t WHERE t.goal.user = :user AND t.completed = true")
    List<Task> findCompletedTasksByUser(User user);

    @Query("SELECT FUNCTION('DATE_FORMAT', t.completedAt, :format) AS period, COUNT(t) " +
            "FROM Task t " +
            "WHERE t.goal.user = :user AND t.completed = true AND t.completedAt IS NOT NULL " +
            "GROUP BY period")
    List<Object[]> countCompletedTasksByPeriod(User user, String format);
}
