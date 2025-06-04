package com.example.pcsbackend.dto;

import com.example.pcsbackend.validation.ProjectDateConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ProjectDateConstraint
public class CreateProjectRequest implements ProjectDateValidatable {

    @NotNull(message = "Name is required")
    @Size(min = 2, message = "Name must be at least 2 characters")
    @Pattern(regexp = "^[A-Za-z].*", message = "Name must start with a letter")
    private String name;

    @NotNull(message = "Start date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "Due date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    @NotNull @Size(min = 1)
    private List<String> userEmails;

    @Override
    public LocalDate getStartDate() { return startDate; }

    @Override
    public LocalDate getDueDate() { return dueDate; }
}
