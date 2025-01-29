package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String profileImg;
}
