package com.example.pcsbackend.validation;

import com.example.pcsbackend.dto.CreateProjectRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProjectDateValidator implements ConstraintValidator<ProjectDateConstraint, CreateProjectRequest> {

    @Override
    public boolean isValid(CreateProjectRequest dto, ConstraintValidatorContext context) {
        if (dto.getStartDate() == null || dto.getDueDate() == null) {
            return true; // это обрабатывается другими аннотациями
        }
        return dto.getStartDate().isBefore(dto.getDueDate());
    }
}
