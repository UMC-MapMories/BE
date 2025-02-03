package com.example.demo.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.dto.FriendshipDto;
import com.example.demo.dto.UserDTO;
import com.example.demo.service.FriendshipService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.example.demo.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Autowired
    private JWTUtil jwtUtil;

    // 친구 목록 조회 (친구 이름으로)
    @GetMapping("/search")
    public ApiResponse<List<UserDTO>> searchUsers(@RequestParam String name, HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        List<UserDTO> users = friendshipService.findUserByName(name, userId);
        return ApiResponse.onSuccess(users);
    }

    // 친구 신청하기
    @PostMapping("/sendRequest")
    public ApiResponse<FriendshipDto> sendFriendRequest(@RequestParam Long toUserId, HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        FriendshipDto friend = friendshipService.sendFriendRequest(userId, toUserId);
        return ApiResponse.onSuccess(friend);
    }

    // 받은 친구 요청 목록 조회
    @GetMapping("/requests")
    public ApiResponse<List<UserDTO>> getFriendRequests(HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        List<UserDTO> fromUsers = friendshipService.getReceivedFriendRequests(userId);
        return ApiResponse.onSuccess(fromUsers);
    }

    // 친구 요청 수락
    @PostMapping("/acceptRequest/{fromUserId}")
    public ApiResponse<FriendshipDto> acceptFriendRequest(@PathVariable Long fromUserId,HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        friendshipService.acceptFriendRequest(fromUserId, userId);
        return ApiResponse.onSuccess(null);
    }

    // 친구 삭제
    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteFriend(@RequestParam Long toUserId, HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        friendshipService.deleteFriend(userId, toUserId);
        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/list")
    public ApiResponse<List<UserDTO>> getFriendList(HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        List<UserDTO> friendList = friendshipService.getFriendList(userId);

        return ApiResponse.onSuccess(friendList);

    }
}
