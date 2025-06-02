package com.example.pcsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateTaskRequest {
    @NotBlank
    private String name;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private UUID projectId;

    @NotNull
    private List<UUID> assigneeIds;
}
