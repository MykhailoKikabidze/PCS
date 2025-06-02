package com.example.pcsbackend.repositories;

import com.example.pcsbackend.entities.TaskUser;
import com.example.pcsbackend.entities.TaskUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskUserRepository extends JpaRepository<TaskUser, TaskUserId> {}