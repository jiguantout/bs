package com.community.toolsharing.controller;

import com.community.toolsharing.dto.ApiResponse;
import com.community.toolsharing.model.Announcement;
import com.community.toolsharing.service.AnnouncementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/public")
    public ApiResponse<List<Announcement>> getPublicAnnouncements() {
        List<Announcement> announcements = announcementService.getPublicAnnouncements();
        return ApiResponse.success(announcements);
    }
}
