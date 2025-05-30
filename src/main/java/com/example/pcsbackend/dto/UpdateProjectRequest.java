package com.example.pcsbackend.dto;

import com.example.pcsbackend.validation.ProjectDateConstraint;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@ProjectDateConstraint
public class UpdateProjectRequest implements ProjectDateValidatable {

    @Size(min = 2)
    @Pattern(regexp = "^[A-Za-z].*")
    private String name;

    private LocalDate startDate;
    private LocalDate dueDate;

    /** полный новый список участников (если нужен) */
    private List<@Email String> userEmails;

    @Override
    public LocalDate getStartDate() { return startDate; }

    @Override
    public LocalDate getDueDate() { return dueDate; }
}
