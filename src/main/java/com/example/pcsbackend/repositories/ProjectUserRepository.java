package com.example.pcsbackend.repositories;

import com.example.pcsbackend.entities.ProjectUser;
import com.example.pcsbackend.entities.ProjectUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUserId> {
    boolean existsById_UserIdAndId_ProjectId(UUID userId, UUID projectId);
}
