package com.community.toolsharing.service;

import com.community.toolsharing.dto.AnnouncementRequest;
import com.community.toolsharing.exception.ResourceNotFoundException;
import com.community.toolsharing.model.Announcement;
import com.community.toolsharing.model.User;
import com.community.toolsharing.repository.AnnouncementRepository;
import com.community.toolsharing.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository, UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
    }

    public Announcement create(String adminUsername, AnnouncementRequest request) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", adminUsername));
        Announcement announcement = Announcement.builder()
                .adminId(admin.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .status(1)
                .build();
        return announcementRepository.save(announcement);
    }

    public Announcement update(Long id, AnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        return announcementRepository.save(announcement);
    }

    public void delete(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
        announcementRepository.delete(announcement);
    }

    public List<Announcement> getPublicAnnouncements() {
        return announcementRepository.findByStatusOrderByCreateTimeDesc(1);
    }

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAllByOrderByCreateTimeDesc();
    }
}
