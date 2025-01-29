package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.ProfileResponseDTO;
import com.example.demo.dto.UpdateUserDTO;
import com.example.demo.exception.CustomException;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public UserController(UserRepository userRepository,JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // 프로필 수정 API (이름, 이미지 URL 수정)
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateUserDTO updateUserDTO, HttpServletRequest request) {

        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");


        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token is missing or invalid.");
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);  // JWT에서 ID 추출

        // ID 기반 사용자 프로필 수정
        Optional<User> userOptional = userRepository.findById(userId); // UserRepository에서 ID로 사용자 찾기

        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        // userOptional -> 찾아온 사용자

        User user = userOptional.get();
        user.setName(updateUserDTO.getName());  // 이름 수정
        user.setProfileImg(updateUserDTO.getProfileImg());  // 이미지 URL 수정

        userRepository.save(user);  // 수정된 사용자 정보 저장

        // 3. 성공적인 응답 반환
        return ResponseEntity.ok("Profile updated successfully.");
    }

    // 프로필 조회 API (이름, 이미지 URL 조회)
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDTO> getProfile(HttpServletRequest request) {

        try {
            // 1. JWT 토큰에서 사용자 ID 추출
            String token = request.getHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            String jwtToken = token.split(" ")[1];
            Long userId = jwtUtil.getId(jwtToken);  // JWT에서 ID 추출 (jwtUtil은 JWTUtil 클래스)

            // 2. ID 기반으로 사용자 정보 조회
            Optional<User> userOptional = userRepository.findById(userId);

            // 사용자가 DB에 존재하지 않을 때
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            User user = userOptional.get();

            // 3. 프로필 정보 반환
            ProfileResponseDTO responseDTO = new ProfileResponseDTO(user.getName(), user.getProfileImg());
            return ResponseEntity.ok(responseDTO);
        }catch (Exception e) {
            throw new CustomException("test",400);
        }
    }




}


