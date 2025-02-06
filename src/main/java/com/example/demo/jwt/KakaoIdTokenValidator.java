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

    // 1. ID Token -> email 추출,, 검증 생략
    public static String getEmailFromTokens(String idToken) {
        String[] parts = idToken.split("\\.");
        String payload = new String(Base64.getDecoder().decode(parts[1]));
        JSONObject payloadJson = new JSONObject(payload);
        return payloadJson.getString("email");
    }
}

