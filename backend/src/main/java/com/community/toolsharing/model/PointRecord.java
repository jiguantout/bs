package com.community.toolsharing.model;

import com.community.toolsharing.enums.PointType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "point_records")
public class PointRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PointType type;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
