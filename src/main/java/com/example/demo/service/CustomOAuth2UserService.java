/*
package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.*;
import com.example.demo.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


// Resource 서버 데이터를 받고 -> 데이터를 재조립
// 각각 resource 서버가 제공하는 데이터가 다르므로,,

// db에 저장하고 불러오기

// Oauth2Service 구현
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // 데이터베이스에 접근하기 위해 데이터베이스 UserRepository를 주입
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    // OAuth2User return type/ loadUser = 함수이름
    // OAuth2UserRequest userRequest = resource 서버에서 제공하는 유저 정보 / 변수로 받기
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest); // 객체 변수 만들기

        System.out.println(oAuth2User);

        // Naver -> naver에 대한 dto // 구글 -> 구글에 대한 dto
        // OAuth2Response 구현해서 req dto를 받기

        // 응답값을 다르게 받야아 한다.

        // 구글, 네이버인지 구분하기 위해
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null; // 인터페이스 바구니 만들고, null 초기화

        // 네이버면 네이버에 관한 처리
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes()); // getAttributes() 꺼내서 넣어준다
        }
        else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes()); // getAttributes() 꺼내서 넣어준다
        }
        else {

            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬

        // 변수를 하나 생성
        // Provider 변수를 가지고 provide 특정 + 네이버에서 제공하는 Id를 연결지어서 username을 만들기
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId(); // username은 필요
        User existData = userRepository.findByUsername(username); // 조회한 결과

        // 조회한 결과

        // 한 번도 로그인하지 않은 경우
        if (existData == null) {

            UserEntity userEntity = new UserEntity(); // userEntity 바구니를 만들고

            // Oauth 서버에서 제공받은 데이터를 넣어주기
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setName(oAuth2Response.getName());
            userEntity.setRole("ROLE_USER");

            // DB에 저장
            userRepository.save(userEntity);

            // UserDTO에 담아서 로그인 진행
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        }

        // 한 번이라도 로그인해서 데이터가 존재하는 경우
        else {

            // existData에 대해서 업데이트를 하고 (이메일, 이름에 대해서)
            // 새로 응답받은 값에서 가지고 오기
            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());

            userRepository.save(existData);

            // userDTO DB에 존재하는 데이터를 다 넣어주기
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(existData.getUsername());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(existData.getRole());

            return new CustomOAuth2User(userDTO);
        }

        // DTO에 담아서 return

        UserDTO userDTO = new UserDTO(); // DTO을 만들어서 넘겨주기 위해 생성

        // DTO 필드 초기화
        userDTO.setUsername(username);
        userDTO.setName(oAuth2Response.getName());
        userDTO.setRole("ROLE_USER");

        // CustomOAuth2User에 넘겨주기 = 최종적으로 넘겨줄 DTO
        return new CustomOAuth2User(userDTO);
    }
}

// Service -> Security 등록
// OAauthUser Dto에 담아서 -> 앞단 Provide에 넘겨줘서 로그인 진행


// LoginSuccessHandler가 발급
// JWT 검증
*/