package com.achievix.goal;

import com.achievix.goal.dto.CreateGoalDTO;
import com.achievix.goal.dto.GoalDTO;
import com.achievix.goal.dto.GoalDetailsDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public ResponseEntity<List<GoalDTO>> getUserGoals() {
        List<GoalDTO> goals = goalService.getUserGoals();
        return ResponseEntity.ok(goals);
    }

    @PostMapping
    public ResponseEntity<GoalDTO> createGoal(@Valid @RequestBody CreateGoalDTO createGoalDTO) {
        GoalDTO createdGoal = goalService.createGoal(createGoalDTO);
        return ResponseEntity.ok(createdGoal);
    }

    @GetMapping("/{goalId}/details")
    public ResponseEntity<GoalDetailsDTO> getGoalDetailsById(@PathVariable Long goalId) {
        GoalDetailsDTO goal = goalService.getGoalDetailsById(goalId);
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<GoalDTO> updateGoal(@PathVariable Long goalId, @RequestBody GoalDTO goalDTO) {
        GoalDTO updatedGoal = goalService.updateGoal(goalId, goalDTO);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }
}