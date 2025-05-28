package com.example.pcsbackend.dto;

import com.example.pcsbackend.entities.ProjectUser;
import lombok.Data;

import java.util.UUID;

@Data
public class ProjectUserDTO {
    private UUID userId;
    private UUID projectId;

    public ProjectUserDTO(ProjectUser projectUser) {
        this.userId = projectUser.getId().getUserId();
        this.projectId = projectUser.getId().getProjectId();
    }
}
