package com.example.demo.repository;

import com.example.demo.domain.Diary;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByIsOpenTrue(); //공개된 다이어리 위치 조회

    Diary findByDiaryId(Long diaryId); // 특정 다이어리 id로 조회

    List<Diary> findDiaryByUserId(Long userId); //특정 유저 id의 다이어리

    List<Diary> findDiariesByIsOpenTrue();

    List<Diary>findByIsOpenTrueAndUserIn(List<User> user);
}
