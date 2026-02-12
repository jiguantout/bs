package com.community.toolsharing.controller;

import com.community.toolsharing.dto.ApiResponse;
import com.community.toolsharing.model.Notification;
import com.community.toolsharing.service.NotificationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<List<Notification>> getMyNotifications() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Notification> notifications = notificationService.getMyNotifications(username);
        return ApiResponse.success(notifications);
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        long count = notificationService.getUnreadCount(username);
        return ApiResponse.success(count);
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        notificationService.markAsRead(username, id);
        return ApiResponse.success("Notification marked as read", null);
    }

    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        notificationService.markAllAsRead(username);
        return ApiResponse.success("All notifications marked as read", null);
    }
}
