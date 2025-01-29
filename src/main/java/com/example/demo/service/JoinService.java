package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.JoinDTO;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// 회원가입 로직
@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // 회원가입 완료 -> true / 아니면 false,, 여기서는 간단하게 void

    public void joinProcess(JoinDTO joinDTO) {

        String email = joinDTO.getEmail(); // getter,, 명명 규칙이 있는 듯
        String password = joinDTO.getPassword();

        // 회원가입 할 때 제약 추가
        // 예외 발생

        Boolean isExist = userRepository. existsByEmail(email);
        // isExist 생성 + email 전달
        // true ->  return 종료

        if (isExist) {

            return;
        }

        User data = new User();

        // data 객체에 필드값 설정
        data.setEmail(email);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        // data.setRole("ROLE_ADMIN"); // 일단 강제

        // userRepository에 객체를 저장
        userRepository.save(data);
    }
}