package com.community.toolsharing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolRequest {

    @NotBlank(message = "Tool name is required")
    private String name;

    private String description;

    private String category;

    private String images;

    private String toolCondition;

    private String location;
}
