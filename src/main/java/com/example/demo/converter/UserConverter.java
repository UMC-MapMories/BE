package com.example.demo.converter;

import com.example.demo.domain.User;
import com.example.demo.dto.UserDTO;

public class UserConverter {
    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getEmail(), user.getName(), user.getProfileImg());
    }
}
