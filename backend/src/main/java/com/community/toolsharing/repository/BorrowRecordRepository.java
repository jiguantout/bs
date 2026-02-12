package com.community.toolsharing.repository;

import com.community.toolsharing.enums.BorrowStatus;
import com.community.toolsharing.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByBorrowerId(Long borrowerId);

    List<BorrowRecord> findByOwnerId(Long ownerId);

    List<BorrowRecord> findByToolIdAndStatusIn(Long toolId, List<BorrowStatus> statuses);

    long countByStatus(BorrowStatus status);

    long countByStatusIn(List<BorrowStatus> statuses);

    boolean existsByToolIdAndBorrowerIdAndStatusIn(Long toolId, Long borrowerId, List<BorrowStatus> statuses);
}
