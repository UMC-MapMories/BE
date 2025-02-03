package com.example.demo.service;

import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.converter.FriendshipConverter;
import com.example.demo.converter.UserConverter;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.FriendshipStatus;
import com.example.demo.domain.User;
import com.example.demo.dto.FriendshipDto;
import com.example.demo.dto.UserDTO;
import com.example.demo.exception.CustomException;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    // 친구 이름으로 조회하기
    public List<UserDTO> findUserByName(String nickname, Long fromUserId) {
        List<User> users = userRepository.findByName(nickname);

        // 자기 자신을 제외하고 필터링
        return users.stream()
                .filter(user -> !user.getId().equals(fromUserId)) // 자기 자신 제외
                .map(UserConverter::toDTO)
                .collect(Collectors.toList());
    }

    // 친구 신청하기
    public FriendshipDto sendFriendRequest(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new CustomException(ErrorStatus.CANNOT_SEND_REQUEST_TO_SELF.getMessage(),
                    ErrorStatus.CANNOT_SEND_REQUEST_TO_SELF.getHttpStatus().value());
        }

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new CustomException(ErrorStatus.FROM_USER_NOT_FOUND.getMessage(),
                        ErrorStatus.FROM_USER_NOT_FOUND.getHttpStatus().value()));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new CustomException(ErrorStatus.TO_USER_NOT_FOUND.getMessage(),
                        ErrorStatus.TO_USER_NOT_FOUND.getHttpStatus().value()));

        Optional<Friendship> friend = friendshipRepository.findByUsers(fromUser, toUser);

        if (friend.isPresent()) {
            Friendship friendship = friend.get();

            if (friendship.getStatus() == FriendshipStatus.REJECTED || friendship.getStatus() == FriendshipStatus.DELETED) {
                // 기존 요청을 재활용하되, 신청 방향이 바뀌었는지 확인
                if (!friendship.getFromUser().equals(fromUser)) {
                    // 기존 요청의 fromUser와 toUser가 반대로 되어 있으면 swap
                    friendship.setFromUser(fromUser);
                    friendship.setToUser(toUser);
                }
                friendship.setStatus(FriendshipStatus.PENDING);
                friendship.setCreatedAt(java.time.LocalDateTime.now());
            } else {
                throw new CustomException(ErrorStatus.REQUEST_ALREADY_EXIST.getMessage(),
                        ErrorStatus.REQUEST_ALREADY_EXIST.getHttpStatus().value());
            }

            friendshipRepository.save(friendship);
            return FriendshipConverter.toDTO(friendship);
        }

        // 기존 요청이 없을 경우 새 요청 생성
        Friendship newFriendship = new Friendship(null, fromUser, toUser, FriendshipStatus.PENDING,
                java.time.LocalDateTime.now(), null);

        friendshipRepository.save(newFriendship);
        return FriendshipConverter.toDTO(newFriendship);
    }

    // 친구 삭제하기
    public void deleteFriend(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new CustomException(ErrorStatus._BAD_REQUEST.getMessage(), ErrorStatus._BAD_REQUEST.getHttpStatus().value());
        }

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new CustomException(ErrorStatus.FROM_USER_NOT_FOUND.getMessage(),
                        ErrorStatus.FROM_USER_NOT_FOUND.getHttpStatus().value()));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new CustomException(ErrorStatus.TO_USER_NOT_FOUND.getMessage(),
                        ErrorStatus.TO_USER_NOT_FOUND.getHttpStatus().value()));

        Optional<Friendship> friendship = friendshipRepository.findByFromUserAndToUser(fromUser, toUser);

        if (friendship.isEmpty()) {
            friendship = friendshipRepository.findByFromUserAndToUser(toUser, fromUser);
        }

        friendship.ifPresent(f -> {
            if (f.getStatus() == FriendshipStatus.ACCEPTED) { // 상태가 ACCEPTED인 경우만 변경
                f.setStatus(FriendshipStatus.DELETED);
                f.setRespondedAt(LocalDateTime.now());
                friendshipRepository.save(f);
            }
        });
    }

    //친구 목록 조회
    public List<UserDTO> getFriendList(Long userId) {
        // 요청자의 ID를 기준으로 ACCEPTED 상태의 친구 관계 조회
        List<Friendship> friendships = friendshipRepository.findByUserId(userId, FriendshipStatus.ACCEPTED);

        // 친구 목록 변환
        List<User> friends = friendships.stream()
                .map(friendship -> friendship.getFromUser().getId().equals(userId) ? friendship.getToUser() : friendship.getFromUser())
                .collect(Collectors.toList());

        // UserDTO 변환 후 반환
        return friends.stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail(), user.getName(), user.getProfileImg()))
                .collect(Collectors.toList());
    }

    //받은 친구 신청 조회
    public List<UserDTO> getReceivedFriendRequests(Long userId) {
        // userId로 User 객체를 가져옵니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.TO_USER_NOT_FOUND.getMessage(), ErrorStatus.TO_USER_NOT_FOUND.getHttpStatus().value()));

        // FriendshipRepository에서 User 객체와 상태를 전달하여 친구 요청 목록을 조회
        List<Friendship> friendships = friendshipRepository.findByToUserAndStatus(user, FriendshipStatus.PENDING);

        // 신청자(FromUser)를 리스트로 변환
        List<User> requesters = friendships.stream()
                .map(friendship -> friendship.getFromUser())
                .collect(Collectors.toList());

        // UserDTO로 변환하여 반환 (fromUser의 정보를 사용)
        return requesters.stream()
                .map(requester -> new UserDTO(requester.getId(), requester.getEmail(), requester.getName(), requester.getProfileImg()))
                .collect(Collectors.toList());
    }

    //친구 신청 수락
    @Transactional
    public void acceptFriendRequest(Long fromUserId, Long toUserId) {
        Friendship friendship = friendshipRepository.findByFromUserIdAndToUserId(fromUserId, toUserId)
                .orElseThrow(() -> new CustomException(ErrorStatus.REQUEST_NOT_FOUND.getMessage(),
                        ErrorStatus.REQUEST_NOT_FOUND.getHttpStatus().value()));

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new CustomException(ErrorStatus.REQUEST_NOT_PENDING.getMessage(),
                    ErrorStatus.REQUEST_NOT_PENDING.getHttpStatus().value());
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setRespondedAt(LocalDateTime.now());

        friendshipRepository.save(friendship);
    }
}
