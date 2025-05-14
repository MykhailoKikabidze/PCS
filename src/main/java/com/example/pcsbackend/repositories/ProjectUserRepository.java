package com.example.pcsbackend.repositories;

import com.example.pcsbackend.entities.ProjectUser;
import com.example.pcsbackend.entities.ProjectUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUserId> {}
