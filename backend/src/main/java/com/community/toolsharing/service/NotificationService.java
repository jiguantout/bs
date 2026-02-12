package com.community.toolsharing.service;

import com.community.toolsharing.exception.BusinessException;
import com.community.toolsharing.exception.ResourceNotFoundException;
import com.community.toolsharing.model.Notification;
import com.community.toolsharing.model.User;
import com.community.toolsharing.repository.NotificationRepository;
import com.community.toolsharing.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void createNotification(Long userId, String title, String content, String type, Long relatedId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .type(type)
                .relatedId(relatedId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    public List<Notification> getMyNotifications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return notificationRepository.findByUserIdOrderByCreateTimeDesc(user.getId());
    }

    @Transactional
    public void markAsRead(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        if (!notification.getUserId().equals(user.getId())) {
            throw new BusinessException("You can only mark your own notifications as read");
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsRead(user.getId(), false);
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return notificationRepository.countByUserIdAndIsRead(user.getId(), false);
    }
}
