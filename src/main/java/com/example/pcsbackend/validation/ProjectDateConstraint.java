package com.example.pcsbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProjectDateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectDateConstraint {
    String message() default "Start date must be before due date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
