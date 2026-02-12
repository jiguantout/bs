package com.community.toolsharing.service;

import com.community.toolsharing.dto.AuditRequest;
import com.community.toolsharing.dto.ToolRequest;
import com.community.toolsharing.enums.PointType;
import com.community.toolsharing.enums.ToolStatus;
import com.community.toolsharing.exception.BusinessException;
import com.community.toolsharing.exception.ResourceNotFoundException;
import com.community.toolsharing.model.Tool;
import com.community.toolsharing.model.User;
import com.community.toolsharing.repository.ToolRepository;
import com.community.toolsharing.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ToolService {

    private final ToolRepository toolRepository;
    private final UserRepository userRepository;
    private final PointService pointService;

    public ToolService(ToolRepository toolRepository,
                       UserRepository userRepository,
                       PointService pointService) {
        this.toolRepository = toolRepository;
        this.userRepository = userRepository;
        this.pointService = pointService;
    }

    @Transactional
    public Tool publishTool(String username, ToolRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Tool tool = Tool.builder()
                .userId(user.getId())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .images(request.getImages())
                .toolCondition(request.getToolCondition())
                .location(request.getLocation())
                .status(ToolStatus.PENDING_REVIEW)
                .build();

        Tool savedTool = toolRepository.save(tool);

        pointService.addPoints(user.getId(), 5, PointType.PUBLISH, "Points for publishing tool: " + request.getName());

        return savedTool;
    }

    public List<Tool> getAvailableTools(String keyword, String category) {
        List<Tool> tools;
        if (keyword != null && !keyword.trim().isEmpty()) {
            tools = toolRepository.findByNameContainingAndStatusIn(keyword.trim(),
                    Collections.singletonList(ToolStatus.AVAILABLE));
        } else {
            tools = toolRepository.findByStatus(ToolStatus.AVAILABLE);
        }

        for (Tool tool : tools) {
            enrichOwnerNickname(tool);
        }

        return tools;
    }

    public Tool getToolById(Long id) {
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", id));
        enrichOwnerNickname(tool);
        return tool;
    }

    public List<Tool> getMyTools(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Tool> tools = toolRepository.findByUserId(user.getId());
        for (Tool tool : tools) {
            enrichOwnerNickname(tool);
        }
        return tools;
    }

    @Transactional
    public Tool updateTool(String username, Long id, ToolRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", id));

        if (!tool.getUserId().equals(user.getId())) {
            throw new BusinessException("You can only update your own tools");
        }

        tool.setName(request.getName());
        tool.setDescription(request.getDescription());
        tool.setCategory(request.getCategory());
        tool.setImages(request.getImages());
        tool.setToolCondition(request.getToolCondition());
        tool.setLocation(request.getLocation());
        tool.setUpdateTime(LocalDateTime.now());

        return toolRepository.save(tool);
    }

    @Transactional
    public void deleteTool(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", id));

        if (!tool.getUserId().equals(user.getId())) {
            throw new BusinessException("You can only delete your own tools");
        }

        toolRepository.delete(tool);
    }

    public List<Tool> getAllTools() {
        List<Tool> tools = toolRepository.findAll();
        for (Tool tool : tools) {
            enrichOwnerNickname(tool);
        }
        return tools;
    }

    @Transactional
    public Tool auditTool(Long id, AuditRequest request) {
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", id));

        if ("approve".equalsIgnoreCase(request.getAction())) {
            tool.setStatus(ToolStatus.AVAILABLE);
        } else if ("reject".equalsIgnoreCase(request.getAction())) {
            tool.setStatus(ToolStatus.REJECTED);
        } else {
            throw new BusinessException("Invalid audit action: " + request.getAction());
        }

        tool.setUpdateTime(LocalDateTime.now());
        return toolRepository.save(tool);
    }

    @Transactional
    public Tool forceOffline(Long id) {
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", id));
        tool.setStatus(ToolStatus.OFFLINE);
        tool.setUpdateTime(LocalDateTime.now());
        return toolRepository.save(tool);
    }

    public long getToolCountByStatus(ToolStatus status) {
        return toolRepository.countByStatus(status);
    }

    public long getTotalToolCount() {
        return toolRepository.count();
    }

    private void enrichOwnerNickname(Tool tool) {
        userRepository.findById(tool.getUserId()).ifPresent(user ->
                tool.setOwnerNickname(user.getNickname())
        );
    }
}
