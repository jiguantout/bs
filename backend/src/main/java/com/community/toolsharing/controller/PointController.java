package com.community.toolsharing.controller;

import com.community.toolsharing.dto.ApiResponse;
import com.community.toolsharing.model.PointRecord;
import com.community.toolsharing.model.User;
import com.community.toolsharing.service.PointService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/points")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @GetMapping("/ranking")
    public ApiResponse<List<User>> getRanking() {
        List<User> ranking = pointService.getRanking();
        return ApiResponse.success(ranking);
    }

    @GetMapping("/my")
    public ApiResponse<List<PointRecord>> getMyPoints() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PointRecord> records = pointService.getMyPoints(username);
        return ApiResponse.success(records);
    }
}
