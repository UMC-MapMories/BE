/*
package com.example.demo.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("response"); // 네이버 response 형식
    }

    @Override
    public String getProvider() {

        return "naver";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString(); // 입력받은 데이터에서 Id를 기준으로 toString 반환
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString(); // email key로 반환
    }

    @Override
    public String getName() {

        return attribute.get("name").toString();
    }
}
*/
