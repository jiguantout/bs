package com.community.toolsharing.model;

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
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "borrow_record_id", nullable = false)
    private Long borrowRecordId;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "tool_id", nullable = false)
    private Long toolId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Transient
    private String reviewerNickname;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
