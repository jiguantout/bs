package com.community.toolsharing.repository;

import com.community.toolsharing.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByToolId(Long toolId);

    List<Review> findByReviewerId(Long reviewerId);

    boolean existsByBorrowRecordId(Long borrowRecordId);
}
