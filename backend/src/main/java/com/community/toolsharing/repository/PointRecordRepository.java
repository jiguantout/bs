package com.community.toolsharing.repository;

import com.community.toolsharing.model.PointRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {

    List<PointRecord> findByUserIdOrderByCreateTimeDesc(Long userId);
}
