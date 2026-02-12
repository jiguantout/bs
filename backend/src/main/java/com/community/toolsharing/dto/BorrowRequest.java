package com.community.toolsharing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequest {

    @NotNull(message = "Tool ID is required")
    private Long toolId;

    private String remark;
}
