package com.community.toolsharing.controller;

import com.community.toolsharing.dto.ApiResponse;
import com.community.toolsharing.dto.ReviewRequest;
import com.community.toolsharing.model.Review;
import com.community.toolsharing.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ApiResponse<Review> createReview(@Valid @RequestBody ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Review review = reviewService.createReview(username, request);
        return ApiResponse.success("Review created successfully", review);
    }

    @GetMapping("/tool/{toolId}")
    public ApiResponse<List<Review>> getToolReviews(@PathVariable Long toolId) {
        List<Review> reviews = reviewService.getToolReviews(toolId);
        return ApiResponse.success(reviews);
    }

    @GetMapping("/my")
    public ApiResponse<List<Review>> getMyReviews() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Review> reviews = reviewService.getMyReviews(username);
        return ApiResponse.success(reviews);
    }
}
