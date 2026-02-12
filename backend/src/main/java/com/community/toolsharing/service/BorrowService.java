package com.community.toolsharing.service;

import com.community.toolsharing.dto.BorrowRequest;
import com.community.toolsharing.enums.BorrowStatus;
import com.community.toolsharing.enums.PointType;
import com.community.toolsharing.enums.ToolStatus;
import com.community.toolsharing.exception.BusinessException;
import com.community.toolsharing.exception.ResourceNotFoundException;
import com.community.toolsharing.model.BorrowRecord;
import com.community.toolsharing.model.Tool;
import com.community.toolsharing.model.User;
import com.community.toolsharing.repository.BorrowRecordRepository;
import com.community.toolsharing.repository.ToolRepository;
import com.community.toolsharing.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final ToolRepository toolRepository;
    private final UserRepository userRepository;
    private final PointService pointService;
    private final NotificationService notificationService;

    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                         ToolRepository toolRepository,
                         UserRepository userRepository,
                         PointService pointService,
                         NotificationService notificationService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.toolRepository = toolRepository;
        this.userRepository = userRepository;
        this.pointService = pointService;
        this.notificationService = notificationService;
    }

    @Transactional
    public BorrowRecord applyBorrow(String username, BorrowRequest request) {
        User borrower = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Tool tool = toolRepository.findById(request.getToolId())
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", request.getToolId()));

        if (tool.getStatus() != ToolStatus.AVAILABLE) {
            throw new BusinessException("Tool is not available for borrowing");
        }

        List<BorrowStatus> activeStatuses = Arrays.asList(
                BorrowStatus.APPLIED, BorrowStatus.APPROVED, BorrowStatus.PICKED_UP);

        boolean hasActiveBorrow = borrowRecordRepository.existsByToolIdAndBorrowerIdAndStatusIn(
                request.getToolId(), borrower.getId(), activeStatuses);
        if (hasActiveBorrow) {
            throw new BusinessException("You already have an active borrow request for this tool");
        }

        BorrowRecord record = BorrowRecord.builder()
                .toolId(tool.getId())
                .borrowerId(borrower.getId())
                .ownerId(tool.getUserId())
                .status(BorrowStatus.APPLIED)
                .remark(request.getRemark())
                .build();

        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        notificationService.createNotification(
                tool.getUserId(),
                "New Borrow Request",
                borrower.getNickname() + " wants to borrow your tool: " + tool.getName(),
                "BORROW_APPLY",
                savedRecord.getId()
        );

        pointService.addPoints(borrower.getId(), 2, PointType.BORROW, "Points for applying to borrow tool: " + tool.getName());

        return savedRecord;
    }

    public List<BorrowRecord> getMyBorrows(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<BorrowRecord> records = borrowRecordRepository.findByBorrowerId(user.getId());
        enrichBorrowRecords(records);
        return records;
    }

    public List<BorrowRecord> getReceivedBorrows(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<BorrowRecord> records = borrowRecordRepository.findByOwnerId(user.getId());
        enrichBorrowRecords(records);
        return records;
    }

    @Transactional
    public BorrowRecord approveBorrow(String username, Long id) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", "id", id));

        if (!record.getOwnerId().equals(owner.getId())) {
            throw new BusinessException("Only the tool owner can approve borrow requests");
        }

        if (record.getStatus() != BorrowStatus.APPLIED) {
            throw new BusinessException("Only applied borrow requests can be approved");
        }

        record.setStatus(BorrowStatus.APPROVED);
        record.setApproveTime(LocalDateTime.now());
        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        notificationService.createNotification(
                record.getBorrowerId(),
                "Borrow Request Approved",
                "Your borrow request has been approved. Please arrange pickup.",
                "BORROW_APPROVED",
                record.getId()
        );

        pointService.addPoints(owner.getId(), 3, PointType.LEND, "Points for lending tool");

        return savedRecord;
    }

    @Transactional
    public BorrowRecord rejectBorrow(String username, Long id) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", "id", id));

        if (!record.getOwnerId().equals(owner.getId())) {
            throw new BusinessException("Only the tool owner can reject borrow requests");
        }

        if (record.getStatus() != BorrowStatus.APPLIED) {
            throw new BusinessException("Only applied borrow requests can be rejected");
        }

        record.setStatus(BorrowStatus.REJECTED);
        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        notificationService.createNotification(
                record.getBorrowerId(),
                "Borrow Request Rejected",
                "Your borrow request has been rejected.",
                "BORROW_REJECTED",
                record.getId()
        );

        return savedRecord;
    }

    @Transactional
    public BorrowRecord confirmPickup(String username, Long id) {
        User borrower = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", "id", id));

        if (!record.getBorrowerId().equals(borrower.getId())) {
            throw new BusinessException("Only the borrower can confirm pickup");
        }

        if (record.getStatus() != BorrowStatus.APPROVED) {
            throw new BusinessException("Only approved borrow requests can be picked up");
        }

        record.setStatus(BorrowStatus.PICKED_UP);
        record.setPickupTime(LocalDateTime.now());
        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        Tool tool = toolRepository.findById(record.getToolId())
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", record.getToolId()));
        tool.setStatus(ToolStatus.BORROWED);
        toolRepository.save(tool);

        return savedRecord;
    }

    @Transactional
    public BorrowRecord confirmReturn(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowRecord", "id", id));

        if (!record.getBorrowerId().equals(user.getId()) && !record.getOwnerId().equals(user.getId())) {
            throw new BusinessException("Only the borrower or the tool owner can confirm return");
        }

        if (record.getStatus() != BorrowStatus.PICKED_UP) {
            throw new BusinessException("Only picked-up items can be returned");
        }

        record.setStatus(BorrowStatus.RETURNED);
        record.setReturnTime(LocalDateTime.now());
        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        Tool tool = toolRepository.findById(record.getToolId())
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", record.getToolId()));
        tool.setStatus(ToolStatus.AVAILABLE);
        toolRepository.save(tool);

        pointService.addPoints(record.getBorrowerId(), 2, PointType.RETURN, "Points for returning tool: " + tool.getName());

        return savedRecord;
    }

    public long getActiveBorrowCount() {
        List<BorrowStatus> activeStatuses = Arrays.asList(
                BorrowStatus.APPLIED, BorrowStatus.APPROVED, BorrowStatus.PICKED_UP);
        return borrowRecordRepository.countByStatusIn(activeStatuses);
    }

    public long getTotalBorrowCount() {
        return borrowRecordRepository.count();
    }

    private void enrichBorrowRecords(List<BorrowRecord> records) {
        for (BorrowRecord record : records) {
            toolRepository.findById(record.getToolId()).ifPresent(tool ->
                    record.setToolName(tool.getName())
            );
            userRepository.findById(record.getBorrowerId()).ifPresent(user ->
                    record.setBorrowerNickname(user.getNickname())
            );
            userRepository.findById(record.getOwnerId()).ifPresent(user ->
                    record.setOwnerNickname(user.getNickname())
            );
        }
    }
}
