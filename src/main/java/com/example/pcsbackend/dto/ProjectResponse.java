package com.example.pcsbackend.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {
    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate dueDate;
    /** список e-mail-ов участников */
    private List<String> userEmails;
}
