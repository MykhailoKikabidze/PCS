package com.example.pcsbackend.dto;

import java.time.LocalDate;

public interface ProjectDateValidatable {
    LocalDate getStartDate();
    LocalDate getDueDate();
}