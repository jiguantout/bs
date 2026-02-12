package com.community.toolsharing.model;

import com.community.toolsharing.enums.BorrowStatus;
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
@Table(name = "borrow_records")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tool_id", nullable = false)
    private Long toolId;

    @Column(name = "borrower_id", nullable = false)
    private Long borrowerId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BorrowStatus status;

    @Column(name = "apply_time")
    private LocalDateTime applyTime;

    @Column(name = "approve_time")
    private LocalDateTime approveTime;

    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;

    @Column(name = "return_time")
    private LocalDateTime returnTime;

    @Column(name = "remark", length = 500)
    private String remark;

    @Transient
    private String toolName;

    @Transient
    private String borrowerNickname;

    @Transient
    private String ownerNickname;

    @PrePersist
    protected void onCreate() {
        applyTime = LocalDateTime.now();
    }
}
