package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.JoinDTO;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 회원가입
@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // 회원가입 완료 -> true / 아니면 false
    public void joinProcess(JoinDTO joinDTO) {

        String email = joinDTO.getEmail();
        String password = joinDTO.getPassword();

        // 이메일 형식 검증
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }

        // 비밀번호 검증 (예: 최소 8자, 숫자, 특수문자 포함)
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상, 숫자와 특수문자를 포함해야 합니다.");
        }

        // 이메일 중복 체크
        Boolean isExist = userRepository.existsByEmail(email);
        if (isExist) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User data = new User();

        // data 객체에 필드값 설정
        data.setEmail(email);
        data.setPassword(bCryptPasswordEncoder.encode(password));

        // userRepository 객체를 저장
        userRepository.save(data);
    }

    // 이메일 형식 검증
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // 비밀번호 형식 검증 (최소 8자, 숫자, 특수문자 포함)
    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }
        return hasDigit && hasSpecialChar;
    }
}
