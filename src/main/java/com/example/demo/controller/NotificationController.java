package com.example.demo.controller;

import com.example.demo.dto.NotificationDto;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @GetMapping
    public List<NotificationDto> getAllNotifications() {
        return service.getAllNotifications();
    }

    @GetMapping("/{id}")
    public NotificationDto getNotificationById(@PathVariable Long id) {
        return service.getNotificationById(id);
    }

    @PostMapping
    public NotificationDto createNotification(@RequestBody NotificationDto dto) {
        return service.createNotification(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        service.deleteNotification(id);
    }
}
