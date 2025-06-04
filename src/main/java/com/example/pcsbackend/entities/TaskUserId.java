package com.example.pcsbackend.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUserId implements Serializable {

    private UUID userId;
    private UUID taskId;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof TaskUserId that)) return false;
//        return Objects.equals(userId, that.userId) &&
//                Objects.equals(taskId, that.taskId);
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(userId, taskId);
//    }
}