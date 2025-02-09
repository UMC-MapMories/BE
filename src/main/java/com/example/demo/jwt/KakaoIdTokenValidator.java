package com.example.demo.jwt;

import io.jsonwebtoken.*;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;

public class KakaoIdTokenValidator {

    private static final String CERTS_URL = "https://kauth.kakao.com/.well-known/jwks.json";
    private static final String EXPECTED_ISSUER = "https://kauth.kakao.com";
    private static final String AUDIENCE = "d94809e9a1ea2e0a8d51647b585bf68d";

    // 1. 페이로드 검증 메서드
    public static void verifyPayload(String idToken) throws Exception {
        String[] tokenParts = idToken.split("\\.");
        if (tokenParts.length != 3) {
            throw new Exception("Invalid ID Token");
        }
        // 페이로드 디코딩
        new String(Base64.getDecoder().decode(tokenParts[1]));
    }

    // 2. 공개 키 가져오는 메서드
    public static PublicKey getKakaoPublicKey(String idToken) throws Exception {
        URL url = new URL(CERTS_URL);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        JSONObject jsonResponse = new JSONObject(response.toString());
        String kid = getKeyIdFromToken(idToken);
        PublicKey publicKey = findPublicKeyByKid(jsonResponse, kid);
        if (publicKey == null) {
            throw new Exception("No matching public key found for the given kid");
        }

        return publicKey;
    }

    // 3. ID Token에서 kid 추출하는 메서드
    public static String getKeyIdFromToken(String idToken) {
        String[] parts = idToken.split("\\.");
        String header = new String(Base64.getDecoder().decode(parts[0]));
        JSONObject headerJson = new JSONObject(header);
        return headerJson.getString("kid");
    }

    // 4. 공개 키를 kid에 해당하는 키를 찾아 RSAPublicKey로 변환
    public static PublicKey findPublicKeyByKid(JSONObject jsonResponse, String kid) throws Exception {
        for (Object key : jsonResponse.getJSONArray("keys")) {
            JSONObject keyJson = (JSONObject) key;
            try {
                if (keyJson.getString("kid").equals(kid)) {
                    byte[] nBytes = Base64.getUrlDecoder().decode(keyJson.getString("n"));
                    byte[] eBytes = Base64.getUrlDecoder().decode(keyJson.getString("e"));

                    BigInteger n = new BigInteger(1, nBytes);
                    BigInteger e = new BigInteger(1, eBytes);

                    RSAPublicKeySpec spec = new RSAPublicKeySpec(n, e);

                    KeyFactory factory = KeyFactory.getInstance("RSA");
                    return factory.generatePublic(spec);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return null;
    }

    // 5. 전체 ID Token 검증 메서드
    public static Claims verifyIdToken(String idToken) throws Exception {
        // 페이로드 검증
        verifyPayload(idToken);

        // 공개 키 가져오기
        PublicKey publicKey = getKakaoPublicKey(idToken);

        // JWT 파서 생성 및 검증 수행
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(publicKey)
                .requireIssuer(EXPECTED_ISSUER)
                .requireAudience(AUDIENCE)
                .build();

        try {
            // ID 토큰 파싱 및 검증
            Jws<Claims> jws = jwtParser.parseClaimsJws(idToken);

            // 만료 시간 검증
            Date now = new Date();
            if (jws.getBody().getExpiration().before(now)) {
                throw new ExpiredJwtException(null, null, "Token has expired");
            }
            return jws.getBody();
        } catch (JwtException e) {
            throw new Exception("Invalid ID token: " + e.getMessage());
        }
    }

    public static String getEmailFromClaims(Claims claims) {
        return claims.get("email", String.class);
    }
}





