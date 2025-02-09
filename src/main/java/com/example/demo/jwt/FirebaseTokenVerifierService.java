package com.example.demo.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FirebaseTokenVerifierService {

    // Firebase 인증서 엔드포인트
    private static final String FIREBASE_CERT_URL = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";
    // issuer 형식 (aud 값과 일치)
    private static final String ISSUER_PREFIX = "https://securetoken.google.com/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // kid -> PublicKey 캐싱 (키 만료 시간은 keysExpiryTime 필드에서 관리)
    private final Map<String, PublicKey> cachedKeys = new ConcurrentHashMap<>();
    private long keysExpiryTime = 0; // 밀리초 단위

    public FirebaseTokenVerifierService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 캐싱된 공개키가 없거나 만료되었으면 Firebase 인증서 엔드포인트에서 키를 갱신
     */
    private synchronized void refreshKeysIfNeeded() throws Exception {
        long nowMillis = System.currentTimeMillis();
        if (nowMillis < keysExpiryTime && !cachedKeys.isEmpty()) {
            // 아직 캐시된 공개키가 유효함
            return;
        }

        ResponseEntity<String> response = restTemplate.getForEntity(FIREBASE_CERT_URL, String.class);
        HttpHeaders headers = response.getHeaders();
        String cacheControl = headers.getFirst(HttpHeaders.CACHE_CONTROL);
        long maxAgeSeconds = parseMaxAge(cacheControl);
        keysExpiryTime = nowMillis + maxAgeSeconds * 1000;

        // 응답 본문은 kid -> X509 인증서 문자열(JSON) 형태임
        String body = response.getBody();
        Map<String, String> keyMap = objectMapper.readValue(body, new TypeReference<Map<String, String>>() {});
        cachedKeys.clear();
        for (Map.Entry<String, String> entry : keyMap.entrySet()) {
            String kid = entry.getKey();
            String certString = entry.getValue();
            PublicKey publicKey = convertCertToPublicKey(certString);
            cachedKeys.put(kid, publicKey);
        }
    }

    /**
     * Cache-Control 헤더에서 max-age 값을 파싱
     */
    private long parseMaxAge(String cacheControl) {
        if (cacheControl == null) return 0;
        Pattern pattern = Pattern.compile("max-age=(\\d+)");
        Matcher matcher = pattern.matcher(cacheControl);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return 0;
    }

    /**
     * X509 인증서 문자열을 PublicKey 객체로 변환
     */
    private PublicKey convertCertToPublicKey(String certString) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (InputStream is = new ByteArrayInputStream(certString.getBytes(StandardCharsets.UTF_8))) {
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(is);
            return certificate.getPublicKey();
        }
    }

    /**
     * Firebase ID 토큰을 검증하고, 클레임 세트(JWTClaimsSet)를 반환
     *
     * @param idToken           클라이언트로부터 전달받은 Firebase ID 토큰
     * @param firebaseProjectId Firebase 프로젝트 ID (aud 및 issuer 검증에 사용)
     * @return JWTClaimsSet 토큰의 클레임 세트
     * @throws Exception 검증 실패 시 예외 발생
     */
    public JWTClaimsSet verifyIdToken(String idToken, String firebaseProjectId) throws Exception {
        // 1. 최신 공개키를 캐시에서 갱신 (필요한 경우)
        refreshKeysIfNeeded();

        // 2. 토큰 파싱
        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(idToken);
        } catch (ParseException e) {
            throw new Exception("토큰 파싱 실패", e);
        }

        // 3. 헤더 검증: alg와 kid
        JWSHeader header = signedJWT.getHeader();
        if (header.getAlgorithm() == null || !"RS256".equals(header.getAlgorithm().getName())) {
            throw new Exception("알고리즘이 RS256이 아닙니다.");
        }
        String kid = header.getKeyID();
        if (kid == null || kid.isEmpty()) {
            throw new Exception("kid 값이 없습니다.");
        }
        PublicKey publicKey = cachedKeys.get(kid);
        if (publicKey == null) {
            throw new Exception("kid에 해당하는 공개키를 찾을 수 없습니다.");
        }

        // 4. 서명 검증
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
        if (!signedJWT.verify(verifier)) {
            throw new Exception("토큰 서명 검증에 실패하였습니다.");
        }

        // 5. 페이로드(클레임) 검증
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        Date now = new Date();

        // 만료 시간(exp)
        Date expirationTime = claims.getExpirationTime();
        if (expirationTime == null || now.after(expirationTime)) {
            throw new Exception("토큰이 만료되었습니다.");
        }

        // 발급 시간(iat)
        Date issueTime = claims.getIssueTime();
        if (issueTime == null || now.before(issueTime)) {
            throw new Exception("토큰의 발급 시간이 유효하지 않습니다.");
        }

        // 청중(aud): Firebase 프로젝트 ID와 일치해야 함
        if (claims.getAudience() == null || claims.getAudience().isEmpty() ||
                !firebaseProjectId.equals(claims.getAudience().get(0))) {
            throw new Exception("토큰의 청중(aud)이 올바르지 않습니다.");
        }

        // 발급자(iss)
        String expectedIssuer = ISSUER_PREFIX + firebaseProjectId;
        if (claims.getIssuer() == null || !claims.getIssuer().equals(expectedIssuer)) {
            throw new Exception("토큰의 발급자(iss)가 올바르지 않습니다.");
        }

        // 주체(sub): 비어 있지 않은 문자열이어야 함
        String subject = claims.getSubject();
        if (subject == null || subject.trim().isEmpty()) {
            throw new Exception("토큰의 subject가 올바르지 않습니다.");
        }

        // 인증 시간(auth_time): 클레임에서 가져오며, 과거 시간이어야 함
        Object authTimeObj = claims.getClaim("auth_time");
        if (authTimeObj == null) {
            throw new Exception("auth_time 클레임이 없습니다.");
        }
        Date authTime;
        if (authTimeObj instanceof Number) {
            // UNIX timestamp (초 단위)로 제공되는 경우
            authTime = new Date(((Number) authTimeObj).longValue() * 1000);
        } else if (authTimeObj instanceof Date) {
            authTime = (Date) authTimeObj;
        } else {
            throw new Exception("auth_time 클레임의 형식이 올바르지 않습니다.");
        }
        if (now.before(authTime)) {
            throw new Exception("auth_time이 미래 시각입니다.");
        }

        // 모든 검증을 통과하면 클레임 전체를 반환
        return claims;
    }

    /**
     * 클레임에서 이메일을 추출하는 유틸리티 메소드
     *
     * @param claims JWTClaimsSet
     * @return 이메일 문자열
     * @throws Exception 이메일 클레임이 없으면 예외 발생
     */
    public static String getEmailFromClaims(JWTClaimsSet claims) throws Exception {
        String email = claims.getStringClaim("email");
        if (email == null || email.isEmpty()) {
            throw new Exception("토큰에 이메일 클레임이 없습니다.");
        }
        return email;
    }
}
