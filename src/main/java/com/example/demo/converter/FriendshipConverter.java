package com.example.demo.converter;

import com.example.demo.domain.Friendship;
import com.example.demo.dto.FriendshipDto;

public class FriendshipConverter {

    public static FriendshipDto toDTO(Friendship friendship) {
        return new FriendshipDto(
                friendship.getId(),
                friendship.getFromUser().getName(),
                friendship.getToUser().getName(),
                friendship.getStatus(),
                friendship.getCreatedAt()
        );
    }
}

