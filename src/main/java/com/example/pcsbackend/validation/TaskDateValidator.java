package com.example.pcsbackend.validation;

import com.example.pcsbackend.entities.Task;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaskDateValidator implements ConstraintValidator<TaskDateConstraint, Task> {

    @Override
    public boolean isValid(Task task, ConstraintValidatorContext ctx) {
        if (task == null) return true;

        var project = task.getProject();
        if (project == null) return true;

        var pStart = project.getStartDate();
        var pDue   = project.getDueDate();
        var tStart = task.getStartDate();
        var tDue   = task.getDueDate();

        // Если даты проекта не заданы – пропускаем проверку
        if (pStart == null && pDue == null) return true;

        boolean ok = true;
        if (pStart != null && tStart != null) {
            ok &= !tStart.isBefore(pStart);
        }
        if (pDue != null && tDue != null) {
            ok &= !tDue.isAfter(pDue);
        }
        return ok;
    }
}