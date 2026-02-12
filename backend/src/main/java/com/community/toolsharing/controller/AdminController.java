package com.community.toolsharing.controller;

import com.community.toolsharing.dto.*;
import com.community.toolsharing.enums.ToolStatus;
import com.community.toolsharing.model.Announcement;
import com.community.toolsharing.model.Tool;
import com.community.toolsharing.model.User;
import com.community.toolsharing.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final ToolService toolService;
    private final BorrowService borrowService;
    private final AnnouncementService announcementService;

    public AdminController(UserService userService,
                           ToolService toolService,
                           BorrowService borrowService,
                           AnnouncementService announcementService) {
        this.userService = userService;
        this.toolService = toolService;
        this.borrowService = borrowService;
        this.announcementService = announcementService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardStats> getDashboard() {
        DashboardStats stats = DashboardStats.builder()
                .totalUsers(userService.getUserCount())
                .totalTools(toolService.getTotalToolCount())
                .pendingAuditCount(toolService.getToolCountByStatus(ToolStatus.PENDING_REVIEW))
                .activeBorrows(borrowService.getActiveBorrowCount())
                .totalBorrows(borrowService.getTotalBorrowCount())
                .build();
        return ApiResponse.success(stats);
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ApiResponse.success(users);
    }

    @PutMapping("/users/{id}/status")
    public ApiResponse<User> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        User user = userService.updateUserStatus(id, status);
        return ApiResponse.success("User status updated", user);
    }

    @GetMapping("/tools")
    public ApiResponse<List<Tool>> getAllTools() {
        List<Tool> tools = toolService.getAllTools();
        return ApiResponse.success(tools);
    }

    @PutMapping("/tools/{id}/audit")
    public ApiResponse<Tool> auditTool(@PathVariable Long id, @Valid @RequestBody AuditRequest request) {
        Tool tool = toolService.auditTool(id, request);
        return ApiResponse.success("Tool audit completed", tool);
    }

    @PutMapping("/tools/{id}/offline")
    public ApiResponse<Tool> forceOffline(@PathVariable Long id) {
        Tool tool = toolService.forceOffline(id);
        return ApiResponse.success("Tool forced offline", tool);
    }

    @GetMapping("/announcements")
    public ApiResponse<List<Announcement>> getAllAnnouncements() {
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        return ApiResponse.success(announcements);
    }

    @PostMapping("/announcements")
    public ApiResponse<Announcement> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Announcement announcement = announcementService.create(username, request);
        return ApiResponse.success("Announcement created", announcement);
    }

    @PutMapping("/announcements/{id}")
    public ApiResponse<Announcement> updateAnnouncement(@PathVariable Long id,
                                                         @Valid @RequestBody AnnouncementRequest request) {
        Announcement announcement = announcementService.update(id, request);
        return ApiResponse.success("Announcement updated", announcement);
    }

    @DeleteMapping("/announcements/{id}")
    public ApiResponse<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.delete(id);
        return ApiResponse.success("Announcement deleted", null);
    }
}
