package com.example.pcsbackend.services;

import com.example.pcsbackend.dto.*;
import com.example.pcsbackend.entities.*;
import com.example.pcsbackend.exceptions.ProjectNotFoundException;
import com.example.pcsbackend.exceptions.TaskNotFoundException;
import com.example.pcsbackend.exceptions.UserNotExistsException;
import com.example.pcsbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;
    private final TaskUserRepository taskUserRepository;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(request.getProjectId()));

        if (request.getStartDate().isBefore(project.getStartDate())) {
            throw new IllegalArgumentException("Task start date can't be before project start date.");
        }
        if (request.getDueDate() != null && project.getDueDate() != null &&
                request.getDueDate().isAfter(project.getDueDate())) {
            throw new IllegalArgumentException("Task due date can't be after project due date.");
        }

        Task task = Task.builder()
                .name(request.getName())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .project(project)
                .build();

        taskRepository.save(task);

        if (request.getAssigneeIds() != null) {
            for (UUID userId : request.getAssigneeIds()) {
                boolean isInProject = projectUserRepository.existsById_UserIdAndId_ProjectId(userId, project.getId());
                if (!isInProject) {
                    throw new IllegalArgumentException("User " + userId + " is not part of the project.");
                }

                TaskUser taskUser = TaskUser.builder()
                        .id(new TaskUserId(userId, task.getId()))
                        .user(userRepository.getReferenceById(userId))
                        .task(task)
                        .build();

                taskUserRepository.save(taskUser);
            }
        }

        return toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        Project project = task.getProject();

        if (request.getName() != null) {
            task.setName(request.getName());
        }
        if (request.getStartDate() != null) {
            task.setStartDate(request.getStartDate());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        if (request.getAssigneeIds() != null) {
            Set<UUID> incomingIds = new HashSet<>(request.getAssigneeIds());

            Iterator<TaskUser> iterator = task.getTaskUsers().iterator();
            while (iterator.hasNext()) {
                TaskUser existingLink = iterator.next();
                UUID existingUserId = existingLink.getUser().getId();

                if (!incomingIds.contains(existingUserId)) {
                    iterator.remove();
                    taskUserRepository.delete(existingLink);
                }
            }

            Set<UUID> stillPresentIds = task.getTaskUsers().stream()
                    .map(tu -> tu.getUser().getId())
                    .collect(Collectors.toSet());

            for (UUID userId : incomingIds) {
                if (stillPresentIds.contains(userId)) {
                    continue;
                }

                boolean isInProject = projectUserRepository.existsById_UserIdAndId_ProjectId(userId, project.getId());
                if (!isInProject) {
                    throw new IllegalArgumentException("User " + userId + " is not part of the project");
                }

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotExistsException(userId.toString()));

                TaskUserId tuId = new TaskUserId(userId, taskId);
                TaskUser newLink = TaskUser.builder()
                        .id(tuId)
                        .user(user)
                        .task(task)
                        .build();

                task.getTaskUsers().add(newLink);
            }
        }

        return toResponse(task);
    }


    @Transactional
    public void deleteTask(UUID taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        taskRepository.deleteById(taskId);
    }

    public List<TaskResponse> getTasksByProjectId(UUID projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    private TaskResponse toResponse(Task task) {
        List<UserDTO> assignees = task.getTaskUsers().stream()
                .map(TaskUser::getUser)
                .map(UserDTO::new)
                .toList();
        
        return TaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .projectId(task.getProject().getId())
                .assignees(assignees)
                .build();
    }
}
