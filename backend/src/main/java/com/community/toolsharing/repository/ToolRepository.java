package com.community.toolsharing.repository;

import com.community.toolsharing.enums.ToolStatus;
import com.community.toolsharing.model.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {

    List<Tool> findByStatus(ToolStatus status);

    List<Tool> findByUserId(Long userId);

    List<Tool> findByStatusIn(List<ToolStatus> statuses);

    List<Tool> findByNameContainingAndStatusIn(String name, List<ToolStatus> statuses);

    long countByStatus(ToolStatus status);
}
