package com.community.toolsharing.controller;

import com.community.toolsharing.dto.ApiResponse;
import com.community.toolsharing.dto.BorrowRequest;
import com.community.toolsharing.model.BorrowRecord;
import com.community.toolsharing.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping
    public ApiResponse<BorrowRecord> applyBorrow(@Valid @RequestBody BorrowRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BorrowRecord record = borrowService.applyBorrow(username, request);
        return ApiResponse.success("Borrow application submitted successfully", record);
    }

    @GetMapping("/my")
    public ApiResponse<List<BorrowRecord>> getMyBorrows() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<BorrowRecord> records = borrowService.getMyBorrows(username);
        return ApiResponse.success(records);
    }

    @GetMapping("/received")
    public ApiResponse<List<BorrowRecord>> getReceivedBorrows() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<BorrowRecord> records = borrowService.getReceivedBorrows(username);
        return ApiResponse.success(records);
    }

    @PutMapping("/{id}/approve")
    public ApiResponse<BorrowRecord> approveBorrow(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BorrowRecord record = borrowService.approveBorrow(username, id);
        return ApiResponse.success("Borrow request approved", record);
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<BorrowRecord> rejectBorrow(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BorrowRecord record = borrowService.rejectBorrow(username, id);
        return ApiResponse.success("Borrow request rejected", record);
    }

    @PutMapping("/{id}/pickup")
    public ApiResponse<BorrowRecord> confirmPickup(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BorrowRecord record = borrowService.confirmPickup(username, id);
        return ApiResponse.success("Pickup confirmed", record);
    }

    @PutMapping("/{id}/return")
    public ApiResponse<BorrowRecord> confirmReturn(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BorrowRecord record = borrowService.confirmReturn(username, id);
        return ApiResponse.success("Return confirmed", record);
    }
}
