package com.community.toolsharing.service;

import com.community.toolsharing.enums.PointType;
import com.community.toolsharing.exception.ResourceNotFoundException;
import com.community.toolsharing.model.PointRecord;
import com.community.toolsharing.model.User;
import com.community.toolsharing.repository.PointRecordRepository;
import com.community.toolsharing.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointService {

    private final PointRecordRepository pointRecordRepository;
    private final UserRepository userRepository;

    public PointService(PointRecordRepository pointRecordRepository, UserRepository userRepository) {
        this.pointRecordRepository = pointRecordRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addPoints(Long userId, int points, PointType type, String description) {
        PointRecord record = PointRecord.builder()
                .userId(userId)
                .points(points)
                .type(type)
                .description(description)
                .build();
        pointRecordRepository.save(record);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setPoints(user.getPoints() + points);
        userRepository.save(user);
    }

    public List<PointRecord> getMyPoints(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return pointRecordRepository.findByUserIdOrderByCreateTimeDesc(user.getId());
    }

    public List<User> getRanking() {
        List<User> allUsers = userRepository.findAllByOrderByPointsDesc();
        return allUsers.stream().limit(50).collect(Collectors.toList());
    }

    public Integer getUserPoints(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return user.getPoints();
    }
}
