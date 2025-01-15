package com.example.demo.repository;

import com.example.demo.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByIsOpenTrue(); //공개된 다이어리 위치 조회

    Diary findByDiaryId(Long diaryId); // 특정 다이어리 id로 조회
}
