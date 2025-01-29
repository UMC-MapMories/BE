package com.example.demo.service;

import com.example.demo.domain.Notification;
import com.example.demo.dto.NotificationDto;
import com.example.demo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repository;

    public List<NotificationDto> getAllNotifications() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public NotificationDto getNotificationById(Long id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        return toDto(notification);
    }

    public NotificationDto createNotification(NotificationDto dto) {
        Notification notification = new Notification();
        notification.setType(dto.getType());
        notification.setMessage(dto.getMessage());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        Notification savedNotification = repository.save(notification);
        return toDto(savedNotification);
    }

    public void deleteNotification(Long id) {
        repository.deleteById(id);
    }

    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getType(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
