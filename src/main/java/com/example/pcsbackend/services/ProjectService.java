package com.example.pcsbackend.services;

import com.example.pcsbackend.dto.*;
import com.example.pcsbackend.entities.*;
import com.example.pcsbackend.exceptions.ProjectNotFoundException;
import com.example.pcsbackend.exceptions.UserNotExistsException;
import com.example.pcsbackend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;


    @Transactional
    public ProjectResponse createProject(CreateProjectRequest req) {

        Project project = Project.builder()
                .name(req.getName())
                .startDate(req.getStartDate())
                .dueDate(req.getDueDate())
                .build();
        projectRepository.save(project);
        Set<ProjectUser> projectUsers = new HashSet<>(Collections.emptySet());

        for (String email : req.getUserEmails()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotExistsException(email));

            ProjectUser link = ProjectUser.builder()
                    .id(new ProjectUserId(user.getId(), project.getId()))
                    .user(user)
                    .project(project)
                    .build();
            projectUsers.add(link);
        }
        project.setProjectUsers(projectUsers);

        return mapToResponse(project);
    }

    @Transactional
    public List<ProjectResponse> getProjectsByUserEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotExistsException(email));

        return user.getProjectUsers().stream()
                .map(ProjectUser::getProject)
                .distinct()
                .map(this::mapToResponse)
                .toList();
    }


    @Transactional
    public ProjectResponse updateProject(UUID projectId, UpdateProjectRequest req) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (req.getName() != null)        project.setName(req.getName());
        if (req.getStartDate() != null)   project.setStartDate(req.getStartDate());
        if (req.getDueDate() != null)     project.setDueDate(req.getDueDate());

        if (project.getStartDate() != null && project.getDueDate() != null &&
                !project.getStartDate().isBefore(project.getDueDate())) {
            throw new IllegalArgumentException("startDate must be before dueDate");
        }

        if (req.getUserEmails() != null) {

            Set<String> incoming = new HashSet<>(req.getUserEmails());
            project.getProjectUsers().removeIf(link -> {
                boolean toRemove = !incoming.contains(link.getUser().getEmail());
                if (toRemove) projectUserRepository.delete(link);
                return toRemove;
            });

            Set<String> existing = project.getProjectUsers().stream()
                    .map(link -> link.getUser().getEmail())
                    .collect(Collectors.toSet());

            for (String email : incoming) {
                if (existing.contains(email)) continue;

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotExistsException(email));

                ProjectUser link = ProjectUser.builder()
                        .id(new ProjectUserId(user.getId(), project.getId()))
                        .user(user)
                        .project(project)
                        .build();
                projectUserRepository.save(link);
                project.getProjectUsers().add(link);
            }
        }

        return mapToResponse(project);
    }


    @Transactional
    public void deleteProject(UUID projectId) {
        if (!projectRepository.existsById(projectId))
            throw new ProjectNotFoundException(projectId);
        projectRepository.deleteById(projectId);
    }


    private ProjectResponse mapToResponse(Project project) {
        List<String> emails = project.getProjectUsers().stream()
                .map(link -> link.getUser().getEmail())
                .distinct()
                .toList();

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .startDate(project.getStartDate())
                .dueDate(project.getDueDate())
                .userEmails(emails)
                .build();
    }
}
