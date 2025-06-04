package com.example.pcsbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CreateTaskRequest {
    @NotBlank
    private String name;

    @NotNull
    private LocalDate startDate;

    @NotNull
    @JsonProperty("endDate")
    private LocalDate dueDate;

    @NotNull
    @JsonProperty("project_id")
    private UUID projectId;

    @NotNull
    @JsonProperty("assignee_ids")
    private List<UUID> assigneeIds;
}
