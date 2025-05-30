package com.example.pcsbackend.validation;

import com.example.pcsbackend.dto.ProjectDateValidatable;
import com.example.pcsbackend.dto.CreateProjectRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProjectDateValidator implements ConstraintValidator<ProjectDateConstraint, ProjectDateValidatable> {

    @Override
    public boolean isValid(ProjectDateValidatable dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getStartDate() == null || dto.getDueDate() == null) {
            return true;
        }
        return dto.getStartDate().isBefore(dto.getDueDate());
    }
}
