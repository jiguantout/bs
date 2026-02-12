package com.community.toolsharing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequest {

    @NotBlank(message = "Action is required (approve/reject)")
    private String action;

    private String reason;
}
