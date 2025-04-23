package com.example.pcsbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.example.pcsbackend.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    public Optional<User> findByEmail(String email);
    public void deleteByEmail(String email);
    public boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :substring, '%'))")
    List<User> findByEmailContainingIgnoreCase(@Param("substring") String substring);

}
