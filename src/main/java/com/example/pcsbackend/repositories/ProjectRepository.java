package com.example.pcsbackend.repositories;

import com.example.pcsbackend.entities.Project;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {}
