package com.example.pcsbackend.dto;

import com.example.pcsbackend.entities.User;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String name;
    private String surname;
    private String email;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
    }
}
