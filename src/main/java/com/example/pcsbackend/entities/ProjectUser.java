package com.example.pcsbackend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProjectUser {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private ProjectUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
