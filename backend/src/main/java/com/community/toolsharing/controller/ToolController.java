package com.community.toolsharing.controller;

import com.community.toolsharing.dto.ApiResponse;
import com.community.toolsharing.dto.ToolRequest;
import com.community.toolsharing.model.Tool;
import com.community.toolsharing.service.ToolService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
public class ToolController {

    private final ToolService toolService;

    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }

    @GetMapping
    public ApiResponse<List<Tool>> getAvailableTools(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        List<Tool> tools = toolService.getAvailableTools(keyword, category);
        return ApiResponse.success(tools);
    }

    @GetMapping("/{id}")
    public ApiResponse<Tool> getToolById(@PathVariable Long id) {
        Tool tool = toolService.getToolById(id);
        return ApiResponse.success(tool);
    }

    @PostMapping
    public ApiResponse<Tool> publishTool(@Valid @RequestBody ToolRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Tool tool = toolService.publishTool(username, request);
        return ApiResponse.success("Tool published successfully, pending review", tool);
    }

    @PutMapping("/{id}")
    public ApiResponse<Tool> updateTool(@PathVariable Long id, @Valid @RequestBody ToolRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Tool tool = toolService.updateTool(username, id, request);
        return ApiResponse.success("Tool updated successfully", tool);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTool(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        toolService.deleteTool(username, id);
        return ApiResponse.success("Tool deleted successfully", null);
    }

    @GetMapping("/my")
    public ApiResponse<List<Tool>> getMyTools() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Tool> tools = toolService.getMyTools(username);
        return ApiResponse.success(tools);
    }
}
