package com.example.pcsbackend.dto;

import com.example.pcsbackend.entities.Project;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ProjectDTO {
    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate dueDate;

    public ProjectDTO(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.startDate = project.getStartDate();
        this.dueDate = project.getDueDate();
    }
}
