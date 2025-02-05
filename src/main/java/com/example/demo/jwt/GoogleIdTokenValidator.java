package com.example.demo.jwt;

import io.jsonwebtoken.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Iterator;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import java.util.Map;

public class GoogleIdTokenValidator {

    // 구글 공개 키 URL
    private static final String CERTS_URL = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";

    // 1. 구글의 공개 키를 가져오는 메서드
    public static RSAPublicKey getGooglePublicKey(String idToken) throws Exception {
        try {
            System.out.println("200 " + idToken);
            // idToken에서 'kid' 값 추출
            String kid = getKeyIdFromToken(idToken);
            System.out.println("300 " + kid);

            // 공개 키 가져오기
            String publicKey = fetchGooglePublicKey(kid);

            System.out.println("400 " + publicKey);


            // 헤더, 푸터, 공백제거
            String publicKeyPEM = publicKey.replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replaceAll("\\s", "");

            System.out.println("600 " + publicKeyPEM);


            byte[] decodedPublicKey = Base64.getDecoder().decode(publicKeyPEM);


            System.out.println("900 " + decodedPublicKey);

            // X509EncodedKeySpec을 사용하여 공개 키를 RSAPublicKey로 변환
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedPublicKey);


            System.out.println("1000 " + keySpec);

            //  공개 키를 RSAPublicKey로 변환
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            System.out.println("555 " + keyFactory.getClass().getName());

            System.out.println("500 " + keyFactory);


            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);


            System.out.println("2000 " + rsaPublicKey);

            // RSAPublicKey 반환
            return rsaPublicKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. JWT 서명 검증을 하는 메서드
    public static Claims verifyIdToken(String idToken, RSAPublicKey publicKey) {
        // JWT 서명 검증
        return Jwts.parser()
                .setSigningKey(publicKey)  // 구글 공개 키 사용
                .build()
                .parseClaimsJws(idToken)   // ID 토큰 검증
                .getBody();
    }

    // 3. JWT 클레임에서 이메일을 추출하는 메서드
    public static String getEmailFromClaims(Claims claims) {
        return claims.get("email", String.class);  // email 필드 가져오기
    }

    // 4. JWT 클레임에서 사용자 ID를 추출하는 메서드
    public static String getUserIdFromClaims(Claims claims) {
        return claims.get("sub", String.class);    // sub (사용자 고유 ID) 필드 가져오기
    }

    // idToken에서 'kid' 추출하는 로직
    // idToken에서 'kid' 추출하는 메서드

    public static String getKeyIdFromToken(String idToken) throws Exception {
        // JWT는 '.'으로 구분된 세 부분으로 나뉨 -> 헤더, 페이로드, 서명
        String[] parts = idToken.split("\\.");

        if (parts.length != 3) {
            throw new Exception("Invalid ID Token");
        }

        // 첫 번째 부분은 헤더, Base64Url로 인코딩된 문자열임
        String header = parts[0];

        // Base64Url 디코딩
        byte[] decodedBytes = Base64.getUrlDecoder().decode(header);

        // 디코딩된 바이트를 JSON 형식으로 변환
        String decodedHeader = new String(decodedBytes, "UTF-8");

        // JSON에서 kid 값 추출
        JSONObject jsonHeader = new JSONObject(decodedHeader);
        if (jsonHeader.has("kid")) {
            return jsonHeader.getString("kid");
        } else {
            throw new Exception("No 'kid' found in token header");
        }
    }

    // 구글 공개 키 가져오기
    public static String fetchGooglePublicKey(String kid) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(CERTS_URL, Map.class);

        Map<String, String> keys = response.getBody();

        if (keys != null && keys.containsKey(kid)) {
            return keys.get(kid); // kid에 해당하는 공개 키 반환
        }
        throw new IllegalArgumentException("No public key found for kid: " + kid);
    }


}
