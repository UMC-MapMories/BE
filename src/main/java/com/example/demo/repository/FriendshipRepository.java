package com.example.demo.repository;

import com.example.demo.domain.Friendship;
import com.example.demo.domain.FriendshipStatus;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // 특정 친구 관계 찾기 (중복 방지)
    Optional<Friendship> findByFromUserAndToUser(User fromUser, User toUser);
    Optional<Friendship> findByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

    // 받은 친구 요청 목록 조회
    List<Friendship> findByToUserAndStatus(User toUser, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.fromUser.id = :userId OR f.toUser.id = :userId) AND f.status = :status")
    List<Friendship> findByUserId(@Param("userId") Long userId, @Param("status") FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.fromUser = :user1 AND f.toUser = :user2) OR (f.fromUser = :user2 AND f.toUser = :user1)")
    Optional<Friendship> findByUsers(@Param("user1") User user1, @Param("user2") User user2);

}
