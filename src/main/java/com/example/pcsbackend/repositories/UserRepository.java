package com.example.pcsbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import com.example.pcsbackend.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    public Optional<User> findByEmail(String email);
    public void deleteByEmail(String email);
    public boolean existsByEmail(String email);
}
