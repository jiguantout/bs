package com.community.toolsharing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {

    private long totalUsers;
    private long totalTools;
    private long pendingAuditCount;
    private long activeBorrows;
    private long totalBorrows;
}
