package com.example.pcsbackend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TaskUser {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private TaskUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}