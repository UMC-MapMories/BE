package com.example.demo.dto;

import com.example.demo.domain.FriendshipStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipDto {
    private Long id;
    private String fromUserName;
    private String toUserName;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
}
