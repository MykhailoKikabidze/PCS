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
        // 1. Загрузка проекта или выброс ProjectNotFoundException
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        // 2. Обновление простых полей, если они заданы
        if (req.getName() != null) {
            project.setName(req.getName());
        }
        if (req.getStartDate() != null) {
            project.setStartDate(req.getStartDate());
        }
        if (req.getDueDate() != null) {
            project.setDueDate(req.getDueDate());
        }

        // 3. Проверка валидности дат
        if (project.getStartDate() != null
                && project.getDueDate() != null
                && !project.getStartDate().isBefore(project.getDueDate())) {
            throw new IllegalArgumentException("startDate must be before dueDate");
        }

        // 4. Синхронизация списка участников, если пришёл список email-ов
        if (req.getUserEmails() != null) {
            Set<String> incomingEmails = new HashSet<>(req.getUserEmails());

            // 4a. Удаляем все связи, email которых отсутствует в incomingEmails
            Iterator<ProjectUser> iterator = project.getProjectUsers().iterator();
            while (iterator.hasNext()) {
                ProjectUser existingLink = iterator.next();
                String existingEmail = existingLink.getUser().getEmail();
                if (!incomingEmails.contains(existingEmail)) {
                    // удаляем из коллекции и помечаем на удаление в БД
                    iterator.remove();
                    projectUserRepository.delete(existingLink);
                }
            }

            // 4b. Собираем email-ы, которые уже остались в коллекции
            Set<String> stillPresent = project.getProjectUsers().stream()
                    .map(link -> link.getUser().getEmail())
                    .collect(Collectors.toSet());

            // 4c. Для каждого нового email создаём связь, если её ещё нет
            for (String email : incomingEmails) {
                if (stillPresent.contains(email)) {
                    continue; // уже есть — пропускаем
                }

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotExistsException(email));

                // проверим ещё раз на случай параллельных изменений,
                // чтобы не попытаться добавить дубликат в БД
                boolean alreadyLinked = projectUserRepository
                        .existsById_UserIdAndId_ProjectId(user.getId(), project.getId());
                if (alreadyLinked) {
                    continue;
                }

                ProjectUser newLink = ProjectUser.builder()
                        .id(new ProjectUserId(user.getId(), project.getId()))
                        .user(user)
                        .project(project)
                        .build();

                // добавляем в коллекцию — благодаря cascade = ALL Hibernate вставит запись в БД при flush
                project.getProjectUsers().add(newLink);
            }
        }

        // 5. Возвращаем DTO; Hibernate автоматически сохранит все изменения (удаления и вставки) по аннотации @Transactional
        return mapToResponse(project);
    }



    @Transactional
    public void deleteProject(UUID projectId) {
        if (!projectRepository.existsById(projectId))
            throw new ProjectNotFoundException(projectId);
        projectRepository.deleteById(projectId);
    }


    private ProjectResponse mapToResponse(Project project) {
        // старый list of emails
        List<String> emails = project.getProjectUsers().stream()
                .map(link -> link.getUser().getEmail())
                .distinct()
                .toList();

        // новый list of initials, например "MK"
        List<String> initials = project.getProjectUsers().stream()
                .map(link -> {
                    String n = link.getUser().getName();
                    String s = link.getUser().getSurname();
                    // берем первый символ каждого и делаем заглавным
                    return ("" + n.charAt(0) + s.charAt(0)).toUpperCase();
                })
                .distinct()
                .toList();

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .startDate(project.getStartDate())
                .dueDate(project.getDueDate())
                .userEmails(emails)
                .userInitials(initials)
                .build();
    }

    public List<UserDTO> getUsersInProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        return project.getProjectUsers().stream()
                .map(projectUser -> new UserDTO(projectUser.getUser()))
                .toList();
    }
}
