package com.achievix.goal;

import com.achievix.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUser(User user);
    List<Goal> findByUserId(Long userId);

    @Query("SELECT COUNT(g) FROM Goal g WHERE g.user = :user AND g.currentValue >= g.targetValue")
    int countCompletedGoalsByUser(User user);

    @Query("SELECT COUNT(g) FROM Goal g WHERE g.user = :user AND g.currentValue < g.targetValue")
    int countActiveGoalsByUser(User user);

    @Query("SELECT g FROM Goal g WHERE g.user = :user AND g.currentValue >= g.targetValue")
    List<Goal> findCompletedGoalsByUser(User user);

    @Query("SELECT FUNCTION('DATE_FORMAT', g.updatedAt, :format) AS period, COUNT(g) " +
            "FROM Goal g " +
            "WHERE g.user = :user AND g.currentValue >= g.targetValue AND g.updatedAt IS NOT NULL " +
            "GROUP BY period")
    List<Object[]> countCompletedGoalsByPeriod(User user, String format);
}
