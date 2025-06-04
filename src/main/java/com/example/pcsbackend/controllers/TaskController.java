package com.example.pcsbackend.controllers;

import com.example.pcsbackend.dto.*;
import com.example.pcsbackend.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }
}
