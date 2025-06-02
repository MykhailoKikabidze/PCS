package com.example.pcsbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TaskResponse {
    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate dueDate;
    private UUID projectId;
    private List<UserDTO> assignees;
}