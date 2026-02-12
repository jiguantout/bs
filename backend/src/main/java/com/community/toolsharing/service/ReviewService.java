package com.community.toolsharing.service;

import com.community.toolsharing.dto.ReviewRequest;
import com.community.toolsharing.enums.BorrowStatus;
import com.community.toolsharing.enums.PointType;
import com.community.toolsharing.exception.BusinessException;
import com.community.toolsharing.exception.ResourceNotFoundException;
import com.community.toolsharing.model.BorrowRecord;
import com.community.toolsharing.model.Review;
import com.community.toolsharing.model.User;
import com.community.toolsharing.repository.BorrowRecordRepository;
import com.community.toolsharing.repository.ReviewRepository;
import com.community.toolsharing.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final PointService pointService;

    public ReviewService(ReviewRepository reviewRepository,
                         BorrowRecordRepository borrowRecordRepository,
                         UserRepository userRepository,
                         PointService pointService) {
        this.reviewRepository = reviewRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.userRepository = userRepository;
        this.pointService = pointService;
    }

    @Transactional
    public Review createReview(String username, ReviewRequest request) {
        User reviewer = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        BorrowRecord borrowRecord = borrowRecordRepository.findById(request.getBorrowRecordId())
                .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", "id", request.getBorrowRecordId()));

        if (borrowRecord.getStatus() != BorrowStatus.RETURNED) {
            throw new BusinessException("Can only review after the tool has been returned");
        }

        if (reviewRepository.existsByBorrowRecordId(request.getBorrowRecordId())) {
            throw new BusinessException("A review already exists for this borrow record");
        }

        Review review = Review.builder()
                .borrowRecordId(borrowRecord.getId())
                .reviewerId(reviewer.getId())
                .toolId(borrowRecord.getToolId())
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        Review savedReview = reviewRepository.save(review);
        savedReview.setReviewerNickname(reviewer.getNickname());

        pointService.addPoints(reviewer.getId(), 2, PointType.REVIEW, "Points for writing a review");

        return savedReview;
    }

    public List<Review> getToolReviews(Long toolId) {
        List<Review> reviews = reviewRepository.findByToolId(toolId);
        enrichReviewerNickname(reviews);
        return reviews;
    }

    public List<Review> getMyReviews(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Review> reviews = reviewRepository.findByReviewerId(user.getId());
        enrichReviewerNickname(reviews);
        return reviews;
    }

    private void enrichReviewerNickname(List<Review> reviews) {
        for (Review review : reviews) {
            userRepository.findById(review.getReviewerId()).ifPresent(user ->
                    review.setReviewerNickname(user.getNickname())
            );
        }
    }
}
