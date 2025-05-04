package com.achievix.task;

import com.achievix.task.dto.TaskCreateDTO;
import com.achievix.task.dto.TaskDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<TaskDTO>> getTasksByGoal(@PathVariable Long goalId) {
        List<TaskDTO> tasks = taskService.getTasksByGoal(goalId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskCreateDTO taskCreateDTO) {
        TaskDTO createdTask = taskService.createTask(taskCreateDTO);
        return ResponseEntity.ok(createdTask);
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<TaskDTO> completeTask(@PathVariable Long taskId) {
        TaskDTO completedTask = taskService.completeTask(taskId);
        return ResponseEntity.ok(completedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}