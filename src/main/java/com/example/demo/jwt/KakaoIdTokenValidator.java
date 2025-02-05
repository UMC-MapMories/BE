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

public class KakaoIdTokenValidator {

    private static final String CERTS_URL = "https://kauth.kakao.com/.well-known/jwks.json";
    private static final String EXPECTED_ISSUER = "https://kauth.kakao.com";
    private static final String AUDIENCE = "208313";

    // 1. 페이로드 검증 메서드
    public static void verifyPayload(String idToken, RSAPublicKey nonce) throws Exception {
        String[] tokenParts = idToken.split("\\.");
        if (tokenParts.length != 3) {
            throw new Exception("Invalid ID Token");
        }

        // 페이로드 디코딩
        String payload = new String(Base64.getDecoder().decode(tokenParts[1]));
        JSONObject payloadJson = new JSONObject(payload);

        // iss, aud, exp, nonce 검증
        if (!payloadJson.getString("iss").equals(EXPECTED_ISSUER)) {
            throw new Exception("Invalid issuer (iss)");
        }
        if (!payloadJson.getString("aud").equals(AUDIENCE)) {
            throw new Exception("Invalid audience (aud)");
        }
        if (payloadJson.getLong("exp") <= System.currentTimeMillis() / 1000) {
            throw new Exception("Token has expired (exp)");
        }
        if (!payloadJson.getString("nonce").equals(nonce)) {
            throw new Exception("Invalid nonce");
        }

        System.out.println("Payload validation passed");
    }

    // 2. 공개 키 가져오는 메서드
    public static RSAPublicKey getKakaoPublicKey(String idToken) throws Exception {
        URL url = new URL(CERTS_URL); // 공개 키 URL
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        JSONObject jsonResponse = new JSONObject(response.toString()); // JSON 응답 변환

        // idToken에서 kid 추출
        String kid = getKeyIdFromToken(idToken);

        // jsonResponse에서 kid에 해당하는 공개 키 찾기
        RSAPublicKey publicKey = findPublicKeyByKid(jsonResponse, kid);

        if (publicKey == null) {
            throw new Exception("No matching public key found for the given kid");
        }
        return publicKey;
    }

    // 3. JWT 서명 검증 메서드
    public static void verifySignature(String idToken, RSAPublicKey publicKey) throws Exception {
        try {
            Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(idToken); // 서명 검증
            System.out.println("Signature validation passed");
        } catch (JwtException e) {
            throw new Exception("Invalid JWT signature", e);
        }
    }

    // 4. ID Token에서 kid 추출하는 메서드
    public static String getKeyIdFromToken(String idToken) {
        String[] parts = idToken.split("\\.");
        String header = new String(Base64.getDecoder().decode(parts[0]));
        JSONObject headerJson = new JSONObject(header);
        return headerJson.getString("kid");
    }

    // 5. 공개 키를 kid에 해당하는 키를 찾아 RSAPublicKey로 변환
    public static RSAPublicKey findPublicKeyByKid(JSONObject jsonResponse, String kid) throws Exception {
        for (Object key : jsonResponse.getJSONArray("keys")) {
            JSONObject keyJson = (JSONObject) key;
            if (keyJson.getString("kid").equals(kid)) {
                String modulus = keyJson.getString("n"); // 'n' (modulus)
                String exponent = keyJson.getString("e"); // 'e' (exponent)

                // 'n'과 'e'를 Base64 디코딩
                byte[] modulusBytes = Base64.getUrlDecoder().decode(modulus);
                byte[] exponentBytes = Base64.getUrlDecoder().decode(exponent);

                // KeyFactory를 사용해 공개 키 객체 생성
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(modulusBytes);
                RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

                return publicKey;
            }
        }

        return null;
    }

    // 6. 전체 ID Token 검증 메서드
    public static Claims verifyIdToken(String idToken, RSAPublicKey nonce) throws Exception {
        // 페이로드 검증
        verifyPayload(idToken, nonce);

        // 공개 키 가져오기
        RSAPublicKey publicKey = getKakaoPublicKey(idToken);

        // 서명 검증
        verifySignature(idToken, publicKey);

        // ID 토큰이 유효하다면, JWS에서 클레임을 반환
        return Jwts.parser()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(idToken) // 토큰을 파싱하여 클레임을 반환
                .getBody(); // 클레임을 반환
    }

    public static String getEmailFromClaims(Claims claims) {
        return claims.get("email", String.class);  // 이메일 필드 추출
    }
}

