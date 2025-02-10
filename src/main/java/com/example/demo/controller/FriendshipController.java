package com.example.demo.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.dto.FriendshipDto;
import com.example.demo.dto.UserDTO;
import com.example.demo.service.FriendshipService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "이름으로 친구 검색", description = "전체 유저 목록에서 친구를 이름으로 찾는 API")
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
    @Operation(summary = "친구 신청하기", description = "친구의 id를 가지고 친구 신청 보내는 API")
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
    @Operation(summary = "받은 친구 요청 조회", description = "내가 받은 친구 신청 리스트를 조회하는 API")
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
    @Operation(summary = "받은 친구 요청 수락", description = "받은 친구 요청을 수락하는 API")
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

    @Operation(summary = "받은 친구 요청 거절", description = "받은 친구 요청을 거절하는 API")
    @PostMapping("/rejectRequest/{fromUserId}")
    public ApiResponse<FriendshipDto> rejectFriendRequest(@PathVariable Long fromUserId,HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        friendshipService.rejectFriendRequest(fromUserId, userId);
        return ApiResponse.onSuccess(null);
    }

    // 친구 삭제
    @Operation(summary = "친구 삭제", description = "친구 목록에서 특정인을 삭제하는 API")
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

    @Operation(summary = "친구 목록 조회", description = "나와 친구인 사람의 리스트를 조회하는 API")
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
