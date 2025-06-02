package com.example.pcsbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TaskDateValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskDateConstraint {
    String message() default "Task dates must be within project dates (start ≥ project.start, due ≤ project.due)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}