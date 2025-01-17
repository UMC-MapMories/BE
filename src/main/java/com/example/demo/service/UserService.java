package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
// 사용자 인증 기능을 제공하는 서비스 클래스
public class UserService {

    private final UserRepository userRepository; // userRepository: 사용자 정보를 저장하는 레포지토리
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 로그인: 이메일로 사용자를 찾고 비밀번호를 확인
    @Transactional
    public User login(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email) // UserRepository는 findByEmail() 메서드를 통해 이메일로 사용자를 검색
                .orElseThrow(() -> new Exception("User not found")); // Exception

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Invalid password");
        }

        // bCryptPasswordEncoder.matches(password, user.getPassword())는 사용자가 입력한 비밀번호(password)와
        // 데이터베이스에 저장된 암호화된 비밀번호(user.getPassword())를 비교

        // @Transactional 어노테이션은 이 메서드가 하나의 트랜잭션 내에서 실행

        return user; // 사용자가 이메일로 로그인할 때 비밀번호를 확인하고, 성공하면 사용자 정보를 반환하는 기능
    }

    // 회원가입: 새 사용자 등록
    @Transactional
    public User save(User user) {
        // 비밀번호 암호화
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        // 사용자 저장
        return userRepository.save(user);

        // UserService는 **UserRepository**를 호출하여 JPA를 통해 데이터베이스에 저장합니다.
    }
}
